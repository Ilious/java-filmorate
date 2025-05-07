package ru.yandex.practicum.filmorate.pojo.enums;

import lombok.Getter;

@Getter
public enum AgeRating {
    G("G"),
    PG("PG"),
    PG13("PG-13"),
    R("R"),
    NC17("NC-17");

    private final AgeRating rating;

    AgeRating(String rating) {
        this.rating = AgeRating.valueOf(rating);
    }
}
