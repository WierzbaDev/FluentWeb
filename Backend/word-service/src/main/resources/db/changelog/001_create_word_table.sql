CREATE TABLE IF NOT EXISTS word(
    id BIGSERIAL PRIMARY KEY,
    source_word VARCHAR(255) NOT NULL,
    translation TEXT,
    word_cerf_level SMALLINT NOT NULL
);