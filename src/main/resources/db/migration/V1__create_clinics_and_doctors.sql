CREATE TABLE clinics(
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name    varchar(150) NOT NULL,
    city    varchar(100 ) NOT NULL,
    created_at  timestamptz NOT NULL DEFAULT now()
);


CREATE TABLE doctors(
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    clinic_id   BIGINT NOT NULL REFERENCES clinics(id),
    name    varchar(150) NOT NULL,
    speciality varchar(100) NOT NULL,
    avg_consult_minutes     INT NOT NULL CHECK (avg_consult_minutes > 0),
    created_at      timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_doctors_clinic_id ON doctors (clinic_id);