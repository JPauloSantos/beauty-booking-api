CREATE TABLE reviews (
    id               UUID      NOT NULL PRIMARY KEY,
    client_id        UUID      NOT NULL REFERENCES users(id),
    establishment_id UUID      NOT NULL REFERENCES establishments(id),
    professional_id  UUID      REFERENCES professionals(id),
    appointment_id   UUID      NOT NULL REFERENCES appointments(id),
    rating           INT       NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment          TEXT,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT unique_review_per_appointment UNIQUE (client_id, appointment_id)
);

CREATE INDEX idx_reviews_establishment ON reviews(establishment_id);
CREATE INDEX idx_reviews_professional  ON reviews(professional_id);
