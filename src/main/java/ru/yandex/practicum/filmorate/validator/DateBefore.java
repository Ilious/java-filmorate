package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DateBeforeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DateBefore {

    String message() default "Invalid date ReleaseDate";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int year() default 1895;

    int month() default 12;

    int day() default 28;
}
