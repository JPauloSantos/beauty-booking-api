CREATE TABLE users (
    id            UUID         NOT NULL PRIMARY KEY,
    name          VARCHAR(200) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(50)  NOT NULL,
    phone         VARCHAR(30),
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    active        BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_users_email ON users(email);
