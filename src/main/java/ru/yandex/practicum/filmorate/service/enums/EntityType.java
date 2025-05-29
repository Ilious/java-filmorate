package ru.yandex.practicum.filmorate.service.enums;

import lombok.Getter;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.Arrays;

@Getter
public enum EntityType {
    LIKE("like"),
    REVIEW("review"),
    FRIEND("friend");

    private final String value;

    EntityType(String value) {
        this.value = value;
    }

    public static EntityType fromValue(String value) {
        return Arrays.stream(EntityType.values())
                .filter(v -> value.equalsIgnoreCase(v.getValue()))
                .findFirst()
                .orElseThrow(
                        () -> new ValidationException("Rating doesn't match any type", "Operation", value)
                );
    }
}
