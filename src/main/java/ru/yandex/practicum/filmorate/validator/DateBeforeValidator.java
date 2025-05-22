package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateBeforeValidator implements ConstraintValidator<DateBefore, LocalDate> {

    private LocalDate dateBefore;

    @Override
    public void initialize(DateBefore constraintAnnotation) {
        dateBefore = LocalDate.of(
                constraintAnnotation.year(),
                constraintAnnotation.month(),
                constraintAnnotation.day()
        );
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        if (date == null || date.isBefore(dateBefore))
            return false;
        else return true;
    }
}
