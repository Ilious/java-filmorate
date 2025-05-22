package ru.yandex.practicum.filmorate.dao.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;

@Slf4j
@Getter
public enum AgeRating {
    G("G"),
    PG("PG"),
    PG13("PG-13"),
    R("R"),
    NC17("NC-17");

    private final String value;

    AgeRating(String value) {
        this.value = value;
    }

     public static AgeRating fromValue(String value) {
        for (AgeRating r: AgeRating.values())
            if (value.equalsIgnoreCase(r.value))
                return r;
        throw new EntityNotFoundException("Rating doesn't match any type", "Rating", "name", value);
    }

    public static AgeRating fromValue(long id) {
        for (int i = 0; i < AgeRating.values().length; ++i) {
            if (id == i + 1) {
                return AgeRating.values()[i];
            }
        }
        throw new EntityNotFoundException("Rating doesn't match any type", "Rating", "id", String.valueOf(id));
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
