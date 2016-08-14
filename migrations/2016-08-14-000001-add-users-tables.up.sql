CREATE OR REPLACE FUNCTION update_updated_at_column()
        RETURNS TRIGGER AS '
    BEGIN
        NEW.updated_at = NOW();
        RETURN NEW;
    END;
' LANGUAGE 'plpgsql';

CREATE TABLE users (
    id          serial PRIMARY KEY,
    email       varchar(60) NOT NULL UNIQUE,
    name       varchar(60) NOT NULL,
    created_at  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    password_digest VARCHAR(162) NOT NULL
);
CREATE TRIGGER update_updated_at_users
    BEFORE UPDATE ON users FOR EACH ROW EXECUTE
    PROCEDURE update_updated_at_column();