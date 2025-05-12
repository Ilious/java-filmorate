package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {

    private final String entityName;

    private final String fieldName;

    private final String value;

    public EntityNotFoundException(String message, String entityName, String fieldName, String value) {
        super(message);
        this.entityName = entityName;
        this.fieldName = fieldName;
        this.value = value;
    }
}
