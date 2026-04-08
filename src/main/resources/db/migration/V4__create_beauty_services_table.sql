CREATE TABLE beauty_services (
    id               UUID           NOT NULL PRIMARY KEY,
    name             VARCHAR(200)   NOT NULL,
    description      TEXT,
    duration_minutes INT            NOT NULL,
    price            NUMERIC(10, 2) NOT NULL,
    establishment_id UUID           NOT NULL REFERENCES establishments(id),
    active           BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_services_establishment ON beauty_services(establishment_id);
