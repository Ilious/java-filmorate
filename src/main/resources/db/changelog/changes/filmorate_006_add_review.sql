
CREATE TABLE IF NOT EXISTS review
(
    review_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    content VARCHAR NOT NULL,
    is_positive BOOLEAN NOT NULL,
    user_id BIGINT NOT NULL,
    film_id BIGINT NOT NULL,
    useful INTEGER,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS liked_reviews
(
    review_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    estimation INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    PRIMARY KEY (review_id, user_id)
);