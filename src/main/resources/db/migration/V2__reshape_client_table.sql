DROP TABLE IF EXISTS client;

CREATE TABLE client (
    id UUID PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    prenom VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    telephone VARCHAR(50),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
