package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.util.Arrays;

@Getter
public enum SortBy {
    Year("year"),
    Likes("likes");

    private final String value;

    SortBy(String value) {
        this.value = value;
    }

    public static SortBy fromValue(String value) {
        return Arrays.stream(SortBy.values())
                .filter(v -> v.toString().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InternalServerException("Not found such sort param"));
    }
}
