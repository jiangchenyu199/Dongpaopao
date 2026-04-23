-- dongpaopao PostgreSQL DDL (v1 draft)
-- stack: Spring Boot 4 + PostgreSQL + Redis + JWT
-- note: Redis/JWT configuration is not part of relational DDL.

BEGIN;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =========================
-- 1) Enum definitions
-- =========================

CREATE TYPE user_status_enum AS ENUM (
  'ACTIVE',
  'RESTRICTED',
  'BANNED'
);

CREATE TYPE runner_verify_status_enum AS ENUM (
  'PENDING',
  'APPROVED',
  'REJECTED'
);

CREATE TYPE order_status_enum AS ENUM (
  'PENDING',
  'IN_PROGRESS',
  'COMPLETED',
  'CANCELLED',
  'DISPUTED'
);

CREATE TYPE dispute_status_enum AS ENUM (
  'OPEN',
  'PROCESSING',
  'RESOLVED'
);

CREATE TYPE operator_role_enum AS ENUM (
  'PUBLISHER',
  'RUNNER',
  'ADMIN',
  'SYSTEM'
);

-- =========================
-- 2) Common trigger helper
-- =========================

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =========================
-- 3) Core tables
-- =========================

CREATE TABLE app_user (
  id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  wechat_openid     VARCHAR(64) NOT NULL UNIQUE,
  student_no        VARCHAR(32) UNIQUE,
  phone             VARCHAR(20),
  nickname          VARCHAR(64) NOT NULL DEFAULT '',
  avatar_url        TEXT,
  status            user_status_enum NOT NULL DEFAULT 'ACTIVE',
  can_publish       BOOLEAN NOT NULL DEFAULT TRUE,
  can_run           BOOLEAN NOT NULL DEFAULT FALSE,
  is_admin          BOOLEAN NOT NULL DEFAULT FALSE,
  last_login_at     TIMESTAMPTZ,
  created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_app_user_status ON app_user(status);
CREATE INDEX idx_app_user_is_admin ON app_user(is_admin);

CREATE TABLE runner_profile (
  user_id                   BIGINT PRIMARY KEY REFERENCES app_user(id) ON DELETE CASCADE,
  verify_status             runner_verify_status_enum NOT NULL DEFAULT 'PENDING',
  verify_reject_reason      VARCHAR(255),
  verified_by_admin_id      BIGINT REFERENCES app_user(id),
  verified_at               TIMESTAMPTZ,
  created_at                TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at                TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_runner_profile_verify_status ON runner_profile(verify_status);

CREATE TABLE errand_order (
  id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  order_no          VARCHAR(32) NOT NULL UNIQUE,
  publisher_id      BIGINT NOT NULL REFERENCES app_user(id),
  runner_id         BIGINT REFERENCES app_user(id),
  title             VARCHAR(120) NOT NULL,
  description       TEXT NOT NULL,
  pickup_address    VARCHAR(255) NOT NULL,
  dropoff_address   VARCHAR(255) NOT NULL,
  reward_amount     NUMERIC(10,2) NOT NULL CHECK (reward_amount > 0),
  deadline_at       TIMESTAMPTZ NOT NULL,
  accepted_at       TIMESTAMPTZ,
  completed_at      TIMESTAMPTZ,
  cancelled_at      TIMESTAMPTZ,
  disputed_at       TIMESTAMPTZ,
  status            order_status_enum NOT NULL DEFAULT 'PENDING',
  cancel_reason     VARCHAR(500),
  version           INTEGER NOT NULL DEFAULT 0,
  created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  CONSTRAINT chk_runner_not_publisher
    CHECK (runner_id IS NULL OR runner_id <> publisher_id)
);

CREATE INDEX idx_errand_order_status ON errand_order(status);
CREATE INDEX idx_errand_order_publisher_status ON errand_order(publisher_id, status);
CREATE INDEX idx_errand_order_runner_status ON errand_order(runner_id, status);
CREATE INDEX idx_errand_order_created_at ON errand_order(created_at DESC);
CREATE INDEX idx_errand_order_deadline_at ON errand_order(deadline_at);

CREATE TABLE order_status_history (
  id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  order_id          BIGINT NOT NULL REFERENCES errand_order(id) ON DELETE CASCADE,
  from_status       order_status_enum,
  to_status         order_status_enum NOT NULL,
  operator_user_id  BIGINT REFERENCES app_user(id),
  operator_role     operator_role_enum NOT NULL,
  reason            VARCHAR(500),
  metadata          JSONB NOT NULL DEFAULT '{}'::jsonb,
  created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_order_status_history_order_id ON order_status_history(order_id, created_at DESC);
CREATE INDEX idx_order_status_history_operator ON order_status_history(operator_user_id, created_at DESC);

CREATE TABLE order_review (
  id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  order_id          BIGINT NOT NULL REFERENCES errand_order(id) ON DELETE CASCADE,
  reviewer_id       BIGINT NOT NULL REFERENCES app_user(id),
  reviewee_id       BIGINT NOT NULL REFERENCES app_user(id),
  score             SMALLINT NOT NULL CHECK (score BETWEEN 1 AND 5),
  content           VARCHAR(500),
  is_anonymous      BOOLEAN NOT NULL DEFAULT FALSE,
  created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  CONSTRAINT uq_order_review_order_reviewer UNIQUE (order_id, reviewer_id),
  CONSTRAINT chk_order_review_not_self CHECK (reviewer_id <> reviewee_id)
);

CREATE INDEX idx_order_review_order_id ON order_review(order_id);
CREATE INDEX idx_order_review_reviewee_id ON order_review(reviewee_id, created_at DESC);

CREATE TABLE dispute (
  id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  order_id          BIGINT NOT NULL UNIQUE REFERENCES errand_order(id) ON DELETE CASCADE,
  initiator_id      BIGINT NOT NULL REFERENCES app_user(id),
  status            dispute_status_enum NOT NULL DEFAULT 'OPEN',
  reason            VARCHAR(500) NOT NULL,
  detail            TEXT,
  evidence          JSONB NOT NULL DEFAULT '[]'::jsonb,
  handler_admin_id  BIGINT REFERENCES app_user(id),
  resolution        VARCHAR(1000),
  resolved_at       TIMESTAMPTZ,
  created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_dispute_status_created_at ON dispute(status, created_at DESC);
CREATE INDEX idx_dispute_initiator_id ON dispute(initiator_id, created_at DESC);

CREATE TABLE audit_log (
  id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  actor_user_id     BIGINT REFERENCES app_user(id),
  actor_type        VARCHAR(20) NOT NULL DEFAULT 'USER',
  action            VARCHAR(64) NOT NULL,
  target_type       VARCHAR(64) NOT NULL,
  target_id         VARCHAR(64) NOT NULL,
  request_id        VARCHAR(64),
  ip_address        INET,
  user_agent        TEXT,
  before_data       JSONB NOT NULL DEFAULT '{}'::jsonb,
  after_data        JSONB NOT NULL DEFAULT '{}'::jsonb,
  extra_data        JSONB NOT NULL DEFAULT '{}'::jsonb,
  created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_log_actor_user_id ON audit_log(actor_user_id, created_at DESC);
CREATE INDEX idx_audit_log_action ON audit_log(action, created_at DESC);
CREATE INDEX idx_audit_log_target ON audit_log(target_type, target_id, created_at DESC);

CREATE TABLE user_session (
  id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  user_id            BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
  refresh_token_hash VARCHAR(128) NOT NULL UNIQUE,
  expires_at         TIMESTAMPTZ NOT NULL,
  revoked_at         TIMESTAMPTZ,
  created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_user_session_user_id_expires_at ON user_session(user_id, expires_at DESC);

-- =========================
-- 4) Triggers for updated_at
-- =========================

CREATE TRIGGER trg_app_user_set_updated_at
BEFORE UPDATE ON app_user
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_runner_profile_set_updated_at
BEFORE UPDATE ON runner_profile
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_errand_order_set_updated_at
BEFORE UPDATE ON errand_order
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_order_review_set_updated_at
BEFORE UPDATE ON order_review
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_dispute_set_updated_at
BEFORE UPDATE ON dispute
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

COMMIT;
