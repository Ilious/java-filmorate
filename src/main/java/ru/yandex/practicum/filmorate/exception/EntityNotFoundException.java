package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {

    private final String entityName;

    private final String value;

    public EntityNotFoundException(String message, String name, String value) {
        super(message);
        this.entityName = name;
        this.value = value;
    }
}
