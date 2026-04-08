CREATE TABLE appointments (
    id               UUID         NOT NULL PRIMARY KEY,
    client_id        UUID         NOT NULL REFERENCES users(id),
    professional_id  UUID         NOT NULL REFERENCES professionals(id),
    service_id       UUID         NOT NULL REFERENCES beauty_services(id),
    establishment_id UUID         NOT NULL REFERENCES establishments(id),
    scheduled_at     TIMESTAMP    NOT NULL,
    end_at           TIMESTAMP    NOT NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    client_notes     TEXT,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_appointments_client       ON appointments(client_id);
CREATE INDEX idx_appointments_professional ON appointments(professional_id);
CREATE INDEX idx_appointments_establishment ON appointments(establishment_id);
CREATE INDEX idx_appointments_scheduled    ON appointments(scheduled_at);
CREATE INDEX idx_appointments_status       ON appointments(status);
