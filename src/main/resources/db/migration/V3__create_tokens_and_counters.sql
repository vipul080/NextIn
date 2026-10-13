CREATE TABLE token_counters(
    doctor_id BIGINT NOT NULL REFERENCES doctors(id),
    queue_date DATE NOT NULL,
    last_number INT NOT NULL DEFAULT 0 CHECK (last_number >= 0),
    PRIMARY KEY (doctor_id, queue_date)
);

CREATE TABLE tokens(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    doctor_id BIGINT NOT NULL REFERENCES doctors(id),
    queue_date DATE NOT NULL,
    token_number INT NOT NULL CHECK (token_number > 0),
    patient_name VARCHAR(250) NOT NULL,
    patient_phone VARCHAR(15) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING'
        CHECK (status IN ('WAITING', 'IN_CONSULTATION', 'DONE', 'SKIPPED', 'CANCELLED')),
    version BIGINT NOT NULL DEFAULT 0,
    created_at timestamptz NOT NULL DEFAULT now(),
    called_at timestamptz,
    completed_at timestamptz,
    UNIQUE(doctor_id, queue_date, token_number)
);

CREATE INDEX idx_tokens_phone_date ON tokens (patient_phone, queue_date);
