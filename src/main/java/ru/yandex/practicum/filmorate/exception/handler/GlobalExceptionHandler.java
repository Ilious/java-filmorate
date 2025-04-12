package ru.yandex.practicum.filmorate.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.EntityExistsException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException exception) {
        log.warn("handleEntityNotFoundException {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleAlreadyExistsException(EntityExistsException exception) {
        log.warn("handleAlreadyExistsException {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleValidationException(EntityNotFoundException exception) {
        log.warn("handleValidationException {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleUnknownException(Exception exception) {
        log.warn("handleUnknownException {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }
}
