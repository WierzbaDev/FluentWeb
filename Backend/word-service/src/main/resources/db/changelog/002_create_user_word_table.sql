CREATE TABLE IF NOT EXISTS user_words(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    word_id BIGINT NOT NULL,
    last_reviewed DATE DEFAULT CURRENT_DATE,
    next_review DATE DEFAULT (CURRENT_DATE + INTERVAL '1 day'),
    interval_days INT DEFAULT 1,
    ease_factor DOUBLE PRECISION DEFAULT 2.5,
    repetition_count INT DEFAULT 0,
    success_count INT DEFAULT 0,
    failed_count INT DEFAULT 0,
    CONSTRAINT user_words_unique UNIQUE (user_id, word_id),
    CONSTRAINT fk_user_words_word FOREIGN KEY (word_id) REFERENCES word(id) ON DELETE CASCADE
);