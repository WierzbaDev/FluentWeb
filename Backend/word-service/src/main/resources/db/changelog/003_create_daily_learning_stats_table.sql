CREATE TABLE IF NOT EXISTS daily_learning_stats(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_correct INT DEFAULT 0,
    total_wrong INT DEFAULT 0,
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    CONSTRAINT daily_learning_stats_unique UNIQUE (user_id, created_at)
);