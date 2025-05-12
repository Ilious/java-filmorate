package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmRecordTest {

    private FilmRecord filmRecord;

    private final Class<ru.yandex.practicum.filmorate.validator.Validator.OnUpdate> onUpdateValidator =
            ru.yandex.practicum.filmorate.validator.Validator.OnUpdate.class;

    private final Class<ru.yandex.practicum.filmorate.validator.Validator.OnCreate> onCreateValidator =
            ru.yandex.practicum.filmorate.validator.Validator.OnCreate.class;

    @Test
    void validate_NameIsNull_ShouldReturnError() {
        filmRecord = new FilmRecord(1L, null, "description", null,
                LocalDate.of(1905, 11, 1), 20, null);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<FilmRecord>> listOnCreate = validator.validate(filmRecord, onCreateValidator);
        Set<ConstraintViolation<FilmRecord>> listOnUpdate = validator.validate(filmRecord, onUpdateValidator);

        assertAll(() -> {
            assertFalse(listOnCreate.isEmpty());
            assertEquals(1, listOnCreate.size());
        });

        assertAll(() -> {
            assertFalse(listOnUpdate.isEmpty());
            assertEquals(1, listOnUpdate.size());
        });
    }

    @Test
    void validate_NameIsBlank_ShouldReturnError() {
        filmRecord = new FilmRecord(1L, "      ", "description", null,
                LocalDate.of(1905, 11, 1), 20, null);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<FilmRecord>> listOnCreate = validator.validate(filmRecord, onCreateValidator);
        Set<ConstraintViolation<FilmRecord>> listOnUpdate = validator.validate(filmRecord, onUpdateValidator);

        assertAll(() -> {
            assertFalse(listOnCreate.isEmpty());
            assertEquals(1, listOnCreate.size());
        });

        assertAll(() -> {
            assertFalse(listOnUpdate.isEmpty());
            assertEquals(1, listOnUpdate.size());
        });
    }

    @Test
    void validate_DescriptionIsNull_ShouldReturnError() {
        filmRecord = new FilmRecord(1L, "name", null, null,
                LocalDate.of(1905, 11, 1), 20, null);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<FilmRecord>> listOnCreate = validator.validate(filmRecord, onCreateValidator);

        assertAll(() -> {
            assertFalse(listOnCreate.isEmpty());
            assertEquals(1, listOnCreate.size());
        });
    }

    @Test
    void validate_DescriptionIsBlank_ShouldReturnError() {
        filmRecord = new FilmRecord(1L, "name", "        ", null,
                LocalDate.of(1905, 11, 1), 20, null);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<FilmRecord>> listOnCreate = validator.validate(filmRecord, onCreateValidator);

        assertAll(() -> {
            assertFalse(listOnCreate.isEmpty());
            assertEquals(1, listOnCreate.size());
        });
    }

    @Test
    void validate_DescriptionIsMoreThan200_ShouldReturnError() {
        filmRecord = new FilmRecord(1L, "name", "n".repeat(201), null,
                LocalDate.of(1905, 11, 1), 20, null);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<FilmRecord>> listOnCreate = validator.validate(filmRecord, onCreateValidator);
        Set<ConstraintViolation<FilmRecord>> listOnUpdate = validator.validate(filmRecord, onUpdateValidator);

        assertAll(() -> {
            assertFalse(listOnCreate.isEmpty());
            assertEquals(1, listOnCreate.size());
        });

        assertAll(() -> {
            assertFalse(listOnUpdate.isEmpty());
            assertEquals(1, listOnUpdate.size());
        });
    }

    @Test
    void validate_DateIsNotValid_ShouldReturnError() {
        filmRecord = new FilmRecord(1L, "name", "desc", null,
                LocalDate.of(1805, 11, 1), 20, null);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<FilmRecord>> listOnCreate = validator.validate(filmRecord, onCreateValidator);
        Set<ConstraintViolation<FilmRecord>> listOnUpdate = validator.validate(filmRecord, onUpdateValidator);

        assertAll(() -> {
            assertFalse(listOnCreate.isEmpty());
            assertEquals(1, listOnCreate.size());
        });

        assertAll(() -> {
            assertFalse(listOnUpdate.isEmpty());
            assertEquals(1, listOnUpdate.size());
        });
    }

    @Test
    void validate_DurationIs0_ShouldReturnError() {
        filmRecord = new FilmRecord(1L, "name", "desc", null,
                LocalDate.of(1905, 11, 1), 0, null);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<FilmRecord>> listOnCreate = validator.validate(filmRecord, onCreateValidator);
        Set<ConstraintViolation<FilmRecord>> listOnUpdate = validator.validate(filmRecord, onUpdateValidator);

        assertAll(() -> {
            assertFalse(listOnCreate.isEmpty());
            assertEquals(1, listOnCreate.size());
        });

        assertAll(() -> {
            assertFalse(listOnUpdate.isEmpty());
            assertEquals(1, listOnUpdate.size());
        });
    }

    @Test
    void validate_DurationIsNegative_ShouldReturnError() {
        filmRecord = new FilmRecord(1L, "name", "desc", null,
                LocalDate.of(1905, 11, 1), -100, null);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<FilmRecord>> listOnCreate = validator.validate(filmRecord, onCreateValidator);
        Set<ConstraintViolation<FilmRecord>> listOnUpdate = validator.validate(filmRecord, onUpdateValidator);

        assertAll(() -> {
            assertFalse(listOnCreate.isEmpty());
            assertEquals(1, listOnCreate.size());
        });

        assertAll(() -> {
            assertFalse(listOnUpdate.isEmpty());
            assertEquals(1, listOnUpdate.size());
        });
    }

    @Test
    void validate_AllFieldsAreCorrect_ShouldNotReturnError() {
        filmRecord = new FilmRecord(1L, "name", "desc", null,
                LocalDate.of(1905, 11, 1), 20, null);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<FilmRecord>> listOnCreate = validator.validate(filmRecord, onCreateValidator);
        Set<ConstraintViolation<FilmRecord>> listOnUpdate = validator.validate(filmRecord, onUpdateValidator);

        assertTrue(listOnCreate.isEmpty());
        assertTrue(listOnUpdate.isEmpty());
    }
}