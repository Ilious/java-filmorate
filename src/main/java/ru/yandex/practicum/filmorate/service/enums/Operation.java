package ru.yandex.practicum.filmorate.service.enums;

import lombok.Getter;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.Arrays;

@Getter
public enum Operation {
    ADD("add"),
    UPDATE("update"),
    REMOVE("remove");

    private final String value;

    Operation(String value) {
        this.value = value;
    }

    public static Operation fromValue(String value) {
        return Arrays.stream(Operation.values())
                .filter(v -> value.equalsIgnoreCase(v.toString()))
                .findFirst()
                .orElseThrow(
                        () -> new ValidationException("Rating doesn't match any type", "Operation", value)
                );
    }
}
