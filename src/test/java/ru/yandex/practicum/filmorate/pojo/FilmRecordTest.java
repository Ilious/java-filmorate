package ru.yandex.practicum.filmorate.pojo;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmRecordTest {

    private FilmRecord filmRecord;

    @Test
    void validate_NameIsNull_ShouldReturnError() {
        filmRecord = new FilmRecord(1L, null, "description",
                LocalDate.of(1905, 11, 1), 20);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<FilmRecord>> list = validator.validate(filmRecord);

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    void validate_NameIsBlank_ShouldReturnError() {
        filmRecord = new FilmRecord(1L, "      ", "description",
                LocalDate.of(1905, 11, 1), 20);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<FilmRecord>> list = validator.validate(filmRecord);

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    void validate_DescriptionIsNull_ShouldReturnError() {
        filmRecord = new FilmRecord(1L, "name", null,
                LocalDate.of(1905, 11, 1), 20);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<FilmRecord>> list = validator.validate(filmRecord);

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    void validate_DescriptionIsBlank_ShouldReturnError() {
        filmRecord = new FilmRecord(1L, "name", "        ",
                LocalDate.of(1905, 11, 1), 20);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<FilmRecord>> list = validator.validate(filmRecord);

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    void validate_DescriptionIsMoreThan200_ShouldReturnError() {
        filmRecord = new FilmRecord(1L, "name", "n".repeat(201),
                LocalDate.of(1905, 11, 1), 20);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<FilmRecord>> list = validator.validate(filmRecord);

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    void validate_DateIsNotValid_ShouldReturnError() {
        filmRecord = new FilmRecord(1L, "name", "desc",
                LocalDate.of(1805, 11, 1), 20);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<FilmRecord>> list = validator.validate(filmRecord);

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    void validate_DurationIs0_ShouldReturnError() {
        filmRecord = new FilmRecord(1L, "name", "desc",
                LocalDate.of(1905, 11, 1), 0);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<FilmRecord>> list = validator.validate(filmRecord);

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    void validate_DurationIsNegative_ShouldReturnError() {
        filmRecord = new FilmRecord(1L, "name", "desc",
                LocalDate.of(1905, 11, 1), -100);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<FilmRecord>> list = validator.validate(filmRecord);

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    void validate_AllFieldsAreCorrect_ShouldNotReturnError() {
        filmRecord = new FilmRecord(1L, "name", "desc",
                LocalDate.of(1905, 11, 1), 20);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<FilmRecord>> list = validator.validate(filmRecord);

        assertTrue(list.isEmpty());
    }
}