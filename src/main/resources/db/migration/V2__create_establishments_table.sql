CREATE TABLE establishments (
    id           UUID         NOT NULL PRIMARY KEY,
    name         VARCHAR(200) NOT NULL,
    description  TEXT,
    street       VARCHAR(300),
    number       VARCHAR(20),
    complement   VARCHAR(100),
    neighborhood VARCHAR(200),
    city         VARCHAR(100),
    state        VARCHAR(50),
    zip_code     VARCHAR(20),
    latitude     DOUBLE PRECISION,
    longitude    DOUBLE PRECISION,
    owner_id     UUID         NOT NULL REFERENCES users(id),
    active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE establishment_photos (
    establishment_id UUID         NOT NULL REFERENCES establishments(id) ON DELETE CASCADE,
    photo_url        VARCHAR(500) NOT NULL
);

CREATE INDEX idx_establishments_owner ON establishments(owner_id);
CREATE INDEX idx_establishments_city  ON establishments(city);
