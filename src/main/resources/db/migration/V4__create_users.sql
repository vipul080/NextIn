-- Staff accounts. Patients do not log in.
-- ADMIN has no clinic; RECEPTIONIST and DOCTOR belong to exactly one clinic.
CREATE TABLE users(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'RECEPTIONIST', 'DOCTOR')),
    clinic_id BIGINT REFERENCES clinics(id),
    created_at timestamptz NOT NULL DEFAULT now(),
    CHECK ((role = 'ADMIN' AND clinic_id IS NULL) OR (role <> 'ADMIN' AND clinic_id IS NOT NULL))
);

CREATE INDEX idx_users_clinic_id ON users (clinic_id);
