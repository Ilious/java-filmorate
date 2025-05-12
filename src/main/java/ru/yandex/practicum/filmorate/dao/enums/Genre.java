package ru.yandex.practicum.filmorate.dao.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;

@Getter
public enum Genre {
    COMEDY("Комедия"),
    DRAM("Драма"),
    CARTOON("Мультфильм"),
    THRILLER("Триллер"),
    DOCUMENTAL("Документальный"),
    ACTION("Боевик");

    private final String value;

    Genre(String value) {
        this.value = value;
    }

    public static Genre fromValue(String value) {
        for (Genre g: Genre.values())
            if (g.value.equalsIgnoreCase(value))
                return g;
        throw new EntityNotFoundException("Genre doesn't match any type", "Genre", "name", value);
    }

    public static Genre fromValue(long id) {
        for (int i = 0; i < Genre.values().length; ++i)
            if (id == i + 1)
                return Genre.values()[i];
        throw new EntityNotFoundException("Genre doesn't match any type", "Genre", "id", String.valueOf(id));
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
