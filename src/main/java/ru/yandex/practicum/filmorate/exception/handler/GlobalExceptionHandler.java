package ru.yandex.practicum.filmorate.exception.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.pojo.ResponseError;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseError handleEntityNotFoundException(EntityNotFoundException exception) {
        String errMessage = String.format("Entity [%s] not found: %s", exception.getEntityName(), exception.getValue());
        log.warn("{}:\n {}", errMessage, exception.getMessage());
        return ResponseError.builder()
                .description(errMessage)
                .code(HttpStatus.NOT_FOUND.value()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handleValidationAnnotationException(MethodArgumentNotValidException exception) {
        StringBuilder errMessage = new StringBuilder("Input fields aren't correct: ");
        String errorFields = exception.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getField)
                .collect(Collectors.joining(", "));
        errMessage.append(errorFields);

        log.warn("{}\n {}", errMessage, exception.getMessage());
        return ResponseError.builder()
                .description(errMessage.toString())
                .code(HttpStatus.BAD_REQUEST.value()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handleRequestBodyIsMissedException(HttpMessageNotReadableException exception) {
        String errMessage = "Input fields are null";

        log.warn("{}\n {}", errMessage, exception.getMessage());
        return ResponseError.builder()
                .description(errMessage)
                .code(HttpStatus.BAD_REQUEST.value()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handleRequestParamsAnnotationsException(ConstraintViolationException exception) {
        StringBuilder errMessage = new StringBuilder("Input fields aren't correct: ");
        String errorFields = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        errMessage.append(errorFields);

        log.warn("{}\n {}", errMessage, exception.getMessage());
        return ResponseError.builder()
                .description(errMessage.toString())
                .code(HttpStatus.BAD_REQUEST.value()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handleMyValidationAnnotationException(ValidationException exception) {
        StringBuilder errMessage = new StringBuilder("Validated fields aren't correct: ");
        errMessage.append(exception.getField()).append(", ").append(exception.getValue());

        log.warn("{}\n {}", errMessage, exception.getMessage());
        return ResponseError.builder()
                .description(errMessage.toString())
                .code(HttpStatus.BAD_REQUEST.value()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseError handleUnknownException(Exception exception) {
        String errMessage = "Error on server";
        log.warn("{}:\n {}", errMessage, exception.getMessage());
        return ResponseError.builder()
                .description(errMessage)
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
    }
}
