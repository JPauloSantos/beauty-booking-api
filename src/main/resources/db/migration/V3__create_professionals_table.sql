CREATE TABLE professionals (
    id               UUID           NOT NULL PRIMARY KEY,
    name             VARCHAR(200)   NOT NULL,
    bio              TEXT,
    establishment_id UUID           NOT NULL REFERENCES establishments(id),
    user_id          UUID           REFERENCES users(id),
    hourly_rate      NUMERIC(10, 2),
    active           BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE TABLE professional_specialties (
    professional_id UUID         NOT NULL REFERENCES professionals(id) ON DELETE CASCADE,
    specialty       VARCHAR(200) NOT NULL
);

CREATE INDEX idx_professionals_establishment ON professionals(establishment_id);
