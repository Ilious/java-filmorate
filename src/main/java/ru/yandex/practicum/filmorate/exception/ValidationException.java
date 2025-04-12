package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {

    private final String field;

    private final String value;

    public ValidationException(String message, String field, String value) {
        super(message);
        this.field = field;
        this.value = value;
    }
}
