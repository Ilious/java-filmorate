package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserRecordTest {

    private UserRecord userRecord;

    private final Class<ru.yandex.practicum.filmorate.validator.Validator.OnCreate> onCreateValidator =
            ru.yandex.practicum.filmorate.validator.Validator.OnCreate.class;

    @Test
    void validate_EmailIsNull_ShouldReturnError() {
        userRecord = new UserRecord(1L, null, "login", "name",
                LocalDate.of(1905, 11, 1));

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserRecord>> list = validator.validate(userRecord);

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    void validate_EmailIsBlank_ShouldReturnError() {
        userRecord = new UserRecord(1L, "         ", "login", "name",
                LocalDate.of(1905, 11, 1));

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserRecord>> list = validator.validate(userRecord);

        assertFalse(list.isEmpty());
        assertTrue(list.stream()
                .allMatch(v -> v.getPropertyPath().toString().equals("email")));
        assertEquals(2, list.size());
    }

    @Test
    void validate_EmailIsNotCorrect_ShouldReturnError() {
        userRecord = new UserRecord(1L, "simple.simple.ru", "login", "name",
                LocalDate.of(1905, 11, 1));

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserRecord>> list = validator.validate(userRecord);

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    void validate_LoginIsNull_ShouldReturnError() {
        userRecord = new UserRecord(1L, "simple@simple.ru", null, "name",
                LocalDate.of(1905, 11, 1));

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserRecord>> list = validator.validate(userRecord);

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    void validate_LoginIsBlank_ShouldReturnError() {
        userRecord = new UserRecord(1L, "simple@simple.ru", "        ", "name",
                LocalDate.of(1905, 11, 1));

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserRecord>> list = validator.validate(userRecord);

        assertFalse(list.isEmpty());
        assertTrue(list.stream()
                .allMatch(v -> v.getPropertyPath().toString().equals("login")));
        assertEquals(2, list.size());
    }

    @Test
    void validate_LoginContainsSpace_ShouldReturnError() {
        userRecord = new UserRecord(1L, "simple@simple.ru", "log in", "name",
                LocalDate.of(1905, 11, 1));

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserRecord>> list = validator.validate(userRecord);

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }


    @Test
    void validate_DateIsInFuture_ShouldReturnError() {
        userRecord = new UserRecord(1L, "simple@simple.ru", "login", "name",
                LocalDate.of(2025, 11, 1));

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserRecord>> list = validator.validate(userRecord, onCreateValidator);

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    void validate_AllFieldsAreCorrect_ShouldReturnError() {
        userRecord = new UserRecord(1L, "simple@simple.ru", "login", "name",
                LocalDate.of(1905, 11, 1));

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserRecord>> list = validator.validate(userRecord);

        assertTrue(list.isEmpty());
    }
}