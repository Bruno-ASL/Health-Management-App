-- Esquema inicial da base de dados.
-- Executado automaticamente pelo PostgreSQL na primeira vez que o container
-- arranca com um volume vazio (ver compose.yaml).

CREATE TYPE event_type AS ENUM ('DOCTOR', 'GYM');
CREATE TYPE event_status AS ENUM ('SCHEDULED', 'COMPLETED', 'CANCELLED');

CREATE TABLE event (
    id        SERIAL PRIMARY KEY,
    name      VARCHAR(200)  NOT NULL,
    type      event_type    NOT NULL,
    date      DATE          NOT NULL,
    time      TIME          NOT NULL,
    duration  INTEGER       NOT NULL CHECK (duration > 0),  -- minutos
    status    event_status  NOT NULL DEFAULT 'SCHEDULED'
);
