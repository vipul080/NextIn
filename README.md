# NextIn

**A queue-token backend for clinics and OPDs, so patients can wait at home instead of in a crowded waiting room.**

In small clinics and government hospital OPDs, patients often wait 2–4 hours after getting their number, with no idea when they'll be called. People miss their turns and the waiting area gets overcrowded. NextIn manages the clinic queue: a patient takes a token and can see their position and expected wait on their phone. The receptionist calls patients in, skips no-shows and re-queues late arrivals. Patients can arrive just in time instead of everyone crowding the clinic at once.

> **Status:** in progress. The core queue (clinics, doctors, token booking, queue management) works and is tested. Authentication, live updates and the AI assistant are next; see the [roadmap](#roadmap).

## Tech stack

Java 21 · Spring Boot 4 · Spring Data JPA (Hibernate) · PostgreSQL · Flyway · Bean Validation · Maven

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

### Other choices
- **Flyway owns the schema** (versioned SQL migrations); Hibernate runs with `ddl-auto: validate` and only checks that entities match the tables.
- **DTOs (Java records) at the API boundary**: entities never leave the service layer, and requests are validated with Bean Validation.
- **Consistent error responses** from one `@RestControllerAdvice`: 400 validation errors with per-field messages, 404 not found, 409 conflicts, and a generic 500 that logs the real cause without exposing it.
- **"Today" follows the clinic's time zone** through an injected `Clock` (Asia/Kolkata), not the server's.
- **N+1 avoided** on the patient's token lookup with `@EntityGraph`.

## API

| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/api/clinics` | Create a clinic |
| `GET` | `/api/clinics` | List clinics |
| `POST` | `/api/clinics/{clinicId}/doctors` | Add a doctor to a clinic |
| `GET` | `/api/clinics/{clinicId}/doctors` | List a clinic's doctors |
| `POST` | `/api/doctors/{doctorId}/tokens` | Book a token for today |
| `GET` | `/api/tokens/{tokenId}/position` | Position, now serving, people ahead, estimated wait |
| `GET` | `/api/tokens?phone=...` | A patient's tokens for today |
| `POST` | `/api/tokens/{tokenId}/cancel` | Cancel a waiting token |
| `GET` | `/api/doctors/{doctorId}/queue` | Today's active queue for a doctor |
| `POST` | `/api/doctors/{doctorId}/queue/call-next` | Call in the next waiting patient |
| `POST` | `/api/tokens/{tokenId}/complete` | Finish a consultation |
| `POST` | `/api/tokens/{tokenId}/skip` | Mark a no-show |
| `POST` | `/api/tokens/{tokenId}/requeue` | Put a skipped patient back in the queue |

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
2. Copy `.env.example` to `.env` and set `DB_PASSWORD`. The `.env` file is git-ignored.
3. Start the app:
   ```bash
   ./mvnw spring-boot:run
   ```
   Flyway creates the tables on first start. The API runs on `http://localhost:8081`.

## Roadmap

- [x] Clinics and doctors
- [x] Token booking with concurrency control, queue management, wait estimate
- [ ] JWT authentication with patient / receptionist / doctor roles
- [ ] Live queue updates over WebSocket or SSE, Redis for the live queue
- [ ] Multilingual AI booking assistant (Spring AI): routes a patient's request to the right speciality and books after confirmation; never diagnoses, and sends emergencies straight to emergency care
- [ ] Integration tests with Testcontainers, Docker setup
