-- ======================================================================
-- WebElec v1 (dépannage électrique) - PostgreSQL schema
-- Objectif: socle simple, lisible, sans fioritures.
-- Compatible Flyway: V1__init_webelec_v1.sql
-- ======================================================================

BEGIN;

-- (Optionnel) Schéma dédié
-- CREATE SCHEMA IF NOT EXISTS webelec;
-- SET search_path TO webelec, public;

-- ======================================================================
-- ENUMS
-- ======================================================================
DO $$ BEGIN
  CREATE TYPE intervention_statut AS ENUM ('EN_COURS', 'RESOLU', 'ECHEC');
EXCEPTION
  WHEN duplicate_object THEN NULL;
END $$;

DO $$ BEGIN
  CREATE TYPE symptome_categorie AS ENUM ('COUPURE', 'DISJONCTION', 'ECLAIRAGE', 'PRISES', 'AUTRE');
EXCEPTION
  WHEN duplicate_object THEN NULL;
END $$;

DO $$ BEGIN
  CREATE TYPE diagnostic_resultat AS ENUM ('NON_TESTE', 'OK', 'NOK');
EXCEPTION
  WHEN duplicate_object THEN NULL;
END $$;

-- ======================================================================
-- TABLE: app_user
-- ======================================================================
CREATE TABLE IF NOT EXISTS app_user (
  id              BIGSERIAL PRIMARY KEY,
  email           VARCHAR(255) NOT NULL UNIQUE,
  password_hash   VARCHAR(255) NOT NULL,
  nom             VARCHAR(120) NOT NULL,
  prenom          VARCHAR(120) NOT NULL,
  created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ======================================================================
-- TABLE: client
-- ======================================================================
CREATE TABLE IF NOT EXISTS client (
  id              BIGSERIAL PRIMARY KEY,
  nom             VARCHAR(255) NOT NULL,
  telephone       VARCHAR(50),
  email           VARCHAR(255),
  adresse         TEXT,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ======================================================================
-- TABLE: symptome
-- ======================================================================
CREATE TABLE IF NOT EXISTS symptome (
  id              BIGSERIAL PRIMARY KEY,
  libelle         VARCHAR(255) NOT NULL,
  description     TEXT,
  categorie       symptome_categorie NOT NULL DEFAULT 'AUTRE'
);

CREATE INDEX IF NOT EXISTS idx_symptome_categorie ON symptome (categorie);

-- ======================================================================
-- TABLE: intervention
-- ======================================================================
CREATE TABLE IF NOT EXISTS intervention (
  id                   BIGSERIAL PRIMARY KEY,
  date_intervention    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  statut               intervention_statut NOT NULL DEFAULT 'EN_COURS',
  commentaire_general  TEXT,

  user_id              BIGINT NOT NULL,
  client_id            BIGINT NOT NULL,
  symptome_id          BIGINT NOT NULL,

  created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),

  CONSTRAINT fk_intervention_user
    FOREIGN KEY (user_id) REFERENCES app_user(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,

  CONSTRAINT fk_intervention_client
    FOREIGN KEY (client_id) REFERENCES client(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,

  CONSTRAINT fk_intervention_symptome
    FOREIGN KEY (symptome_id) REFERENCES symptome(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_intervention_user_id   ON intervention (user_id);
CREATE INDEX IF NOT EXISTS idx_intervention_client_id ON intervention (client_id);
CREATE INDEX IF NOT EXISTS idx_intervention_symptome  ON intervention (symptome_id);
CREATE INDEX IF NOT EXISTS idx_intervention_date      ON intervention (date_intervention);

-- ======================================================================
-- TABLE: diagnostic_step
-- ======================================================================
CREATE TABLE IF NOT EXISTS diagnostic_step (
  id               BIGSERIAL PRIMARY KEY,
  ordre            INTEGER NOT NULL CHECK (ordre >= 1),
  question         TEXT    NOT NULL,
  action_a_faire   TEXT,
  resultat         diagnostic_resultat NOT NULL DEFAULT 'NON_TESTE',
  commentaire      TEXT,

  intervention_id  BIGINT NOT NULL,

  CONSTRAINT fk_diagnostic_step_intervention
    FOREIGN KEY (intervention_id) REFERENCES intervention(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,

  CONSTRAINT uq_diagnostic_step_ordre_par_intervention
    UNIQUE (intervention_id, ordre)
);

CREATE INDEX IF NOT EXISTS idx_diagstep_intervention_id ON diagnostic_step (intervention_id);
CREATE INDEX IF NOT EXISTS idx_diagstep_resultat        ON diagnostic_step (resultat);

-- ======================================================================
-- TABLE: regle_rgie
-- ======================================================================
CREATE TABLE IF NOT EXISTS regle_rgie (
  id              BIGSERIAL PRIMARY KEY,
  code_article    VARCHAR(120) NOT NULL UNIQUE,
  titre           VARCHAR(255) NOT NULL,
  description     TEXT,
  url_reference   TEXT
);

-- ======================================================================
-- TABLE: diagnostic_rgie (N-N)
-- ======================================================================
CREATE TABLE IF NOT EXISTS diagnostic_rgie (
  diagnostic_step_id  BIGINT NOT NULL,
  regle_rgie_id       BIGINT NOT NULL,

  PRIMARY KEY (diagnostic_step_id, regle_rgie_id),

  CONSTRAINT fk_diag_rgie_step
    FOREIGN KEY (diagnostic_step_id) REFERENCES diagnostic_step(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,

  CONSTRAINT fk_diag_rgie_regle
    FOREIGN KEY (regle_rgie_id) REFERENCES regle_rgie(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_diag_rgie_regle_id ON diagnostic_rgie (regle_rgie_id);

-- ======================================================================
-- Données minimales (optionnel): symptômes de base
-- ======================================================================
INSERT INTO symptome (libelle, description, categorie)
VALUES
  ('Plus de courant dans une pièce', 'Une zone est hors tension, autres zones OK.', 'COUPURE'),
  ('Le disjoncteur saute immédiatement', 'Déclenchement dès ré-enclenchement.', 'DISJONCTION'),
  ('L’éclairage clignote', 'Variations ou clignotements visibles.', 'ECLAIRAGE'),
  ('Une prise chauffe', 'Échauffement, odeur, noircissement possible.', 'PRISES')
ON CONFLICT DO NOTHING;

COMMIT;
