CREATE TABLE IF NOT EXISTS learning_stats_archive(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_correct INT DEFAULT 0,
    total_wrong INT DEFAULT 0,
    created_at DATE NOT NULL,
    audit TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);