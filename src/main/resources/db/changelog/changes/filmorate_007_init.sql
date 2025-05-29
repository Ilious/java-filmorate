CREATE TABLE IF NOT EXISTS directors
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        varchar(255) not null
);

CREATE TABLE IF NOT EXISTS film_directors
(
    director_id BIGINT,
    film_id     BIGINT,
    PRIMARY KEY (director_id, film_id),
    foreign key (director_id) references directors (id) on delete cascade,
    foreign key (film_id) references films (id) on delete cascade
);
