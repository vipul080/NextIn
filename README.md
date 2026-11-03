# NextIn

**A queue-token backend for clinics and OPDs, so patients can wait at home instead of in a crowded waiting room.**

In small clinics and government hospital OPDs, patients often wait 2–4 hours after getting their number, with no idea when they'll be called. People miss their turns and the waiting area gets overcrowded. NextIn manages the clinic queue: a patient takes a token and can see their position and expected wait on their phone. The receptionist calls patients in, skips no-shows and re-queues late arrivals. Patients can arrive just in time instead of everyone crowding the clinic at once.

> **Status:** in progress. The core queue (clinics, doctors, token booking, queue management) and staff login with role-based access work and are tested. Live updates and the AI assistant are next; see the [roadmap](#roadmap).

## Tech stack

Java 21 · Spring Boot 4 · Spring Security · JWT (jjwt) · Spring Data JPA (Hibernate) · PostgreSQL · Flyway · Bean Validation · Maven

## Design highlights

### No duplicate token numbers under concurrent bookings
Token numbers restart every day for every doctor. When many patients book the same doctor at once, each must still get a unique number. Booking happens in one transaction:

1. Make sure today's counter row exists for the doctor (`INSERT ... ON CONFLICT DO NOTHING`, so two "first booking of the day" requests can't collide).
2. Lock that counter row with `SELECT ... FOR UPDATE` (pessimistic locking), so concurrent bookings are handled one after another.
3. Increment the counter and insert the token, then commit, which releases the lock.

A `UNIQUE (doctor_id, queue_date, token_number)` constraint is the database-level safety net. Verified with 20 simultaneous booking requests: tokens 1–20, no duplicates.

### Token lifecycle as a state machine
```
WAITING ──call in──► IN_CONSULTATION ──complete──► DONE
   │                        │
   ├──cancel──► CANCELLED   └──skip──► SKIPPED ──requeue──► WAITING
   └──skip────► SKIPPED
```
The allowed moves live inside the `Token` entity, so no caller can bypass them; anything else returns `409 Conflict`. Status updates use optimistic locking (`@Version`), so if two receptionists change the same token at once, the second gets a `409` instead of silently overwriting the first.

### Who can do what
Patients don't log in: many are walk-ins, and booking must be as simple as possible. Staff log in and send a JWT with every request.

| Role | Can |
|---|---|
| Patient (no login) | Book a token, check position, cancel their own token (must give the phone number it was booked with) |
| `RECEPTIONIST`, `DOCTOR` | Run the queue (view, call next, complete, skip, requeue), **only for doctors in their own clinic** |
| `ADMIN` | Create clinics, doctors and staff accounts; access every clinic |

Roles are checked in `SecurityConfig` (which endpoints a role may call). Clinic ownership is checked in the service layer by `ClinicAccessGuard` (a receptionist at clinic 1 gets `403` on clinic 2's queue), because the role alone can't know which clinic a token belongs to.

- **Stateless JWT:** login checks the BCrypt password hash and returns a token signed with HMAC-SHA256 containing the user id, role and clinic. A filter validates it on every request; there are no server-side sessions.
- **Login errors don't reveal which emails exist:** a wrong email and a wrong password give the same `401`.
- **First admin:** created on startup from `ADMIN_EMAIL` / `ADMIN_PASSWORD` if no admin exists; admins can't be created through the API.
- `401` = not logged in or invalid token, `403` = logged in but not allowed, both in the same JSON error format as every other error.

### Other choices
- **Flyway owns the schema** (versioned SQL migrations); Hibernate runs with `ddl-auto: validate` and only checks that entities match the tables.
- **DTOs (Java records) at the API boundary**: entities never leave the service layer, and requests are validated with Bean Validation.
- **Consistent error responses** from one `@RestControllerAdvice`: 400 validation errors with per-field messages, 404 not found, 409 conflicts, and a generic 500 that logs the real cause without exposing it.
- **"Today" follows the clinic's time zone** through an injected `Clock` (Asia/Kolkata), not the server's.
- **N+1 avoided** on the patient's token lookup with `@EntityGraph`.

## API

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| `POST` | `/api/auth/login` | Public | Staff login, returns a JWT |
| `GET` | `/api/clinics`, `/api/clinics/{clinicId}` | Public | List clinics / one clinic |
| `GET` | `/api/clinics/{clinicId}/doctors`, `.../doctors/{doctorId}` | Public | List a clinic's doctors / one doctor |
| `POST` | `/api/doctors/{doctorId}/tokens` | Public | Book a token for today |
| `GET` | `/api/tokens/{tokenId}/position` | Public | Position, now serving, people ahead, estimated wait |
| `GET` | `/api/tokens?phone=...` | Public | A patient's tokens for today |
| `POST` | `/api/tokens/{tokenId}/cancel` | Public + phone | Cancel a waiting token (body: `{"patientPhone": "..."}`) |
| `GET` | `/api/doctors/{doctorId}/queue` | Staff of that clinic | Today's active queue for a doctor |
| `POST` | `/api/doctors/{doctorId}/queue/call-next` | Staff of that clinic | Call in the next waiting patient |
| `POST` | `/api/tokens/{tokenId}/complete` | Staff of that clinic | Finish a consultation |
| `POST` | `/api/tokens/{tokenId}/skip` | Staff of that clinic | Mark a no-show |
| `POST` | `/api/tokens/{tokenId}/requeue` | Staff of that clinic | Put a skipped patient back in the queue |
| `POST` | `/api/clinics` | Admin | Create a clinic |
| `POST` | `/api/clinics/{clinicId}/doctors` | Admin | Add a doctor to a clinic |
| `POST` | `/api/admin/staff` | Admin | Create a receptionist or doctor account |
| `GET` | `/api/admin/clinics/{clinicId}/staff` | Admin | List a clinic's staff |

Staff requests send the token from login as `Authorization: Bearer <accessToken>`.

Example: booking a token

```http
POST /api/doctors/1/tokens
Content-Type: application/json

{ "patientName": "Ramesh", "patientPhone": "9876543210" }
```

```json
{
  "id": 12,
  "doctorId": 1,
  "doctorName": "Dr. Sharma",
  "queueDate": "2026-10-24",
  "tokenNumber": 12,
  "patientName": "Ramesh",
  "status": "WAITING",
  "createdAt": "2026-10-24T04:12:09Z",
  "calledAt": null,
  "completedAt": null
}
```

## Running locally

**Requirements:** Java 21, PostgreSQL.

1. Create a database and a user for it, for example in `psql`:
   ```sql
   CREATE USER nextin WITH PASSWORD 'choose-a-password';
   CREATE DATABASE nextin OWNER nextin;
   ```
2. Copy `.env.example` to `.env` and fill it in: `DB_PASSWORD`, a `JWT_SECRET` of at least 32 characters, and `ADMIN_EMAIL` / `ADMIN_PASSWORD` for the first admin account. The `.env` file is git-ignored.
3. Start the app:
   ```bash
   ./mvnw spring-boot:run
   ```
   Flyway creates the tables on first start. The API runs on `http://localhost:8081`.

## Roadmap

- [x] Clinics and doctors
- [x] Token booking with concurrency control, queue management, wait estimate
- [x] Staff login with JWT, roles (admin / receptionist / doctor) and per-clinic access
- [ ] Live queue updates over WebSocket or SSE, Redis for the live queue
- [ ] Multilingual AI booking assistant (Spring AI): routes a patient's request to the right speciality and books after confirmation; never diagnoses, and sends emergencies straight to emergency care
- [ ] Integration tests with Testcontainers, Docker setup
