package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.filmorate.validator.Validator;

public record ReviewRecord(

        @NotNull(groups = Validator.OnUpdate.class) Long reviewId,

        @NotBlank(groups = {Validator.OnUpdate.class, Validator.OnCreate.class}) String content,

        @NotNull(groups = {Validator.OnUpdate.class, Validator.OnCreate.class})Boolean isPositive,

        @NotNull(groups = {Validator.OnUpdate.class, Validator.OnCreate.class}) Long userId,

        @NotNull(groups = {Validator.OnUpdate.class, Validator.OnCreate.class}) Long filmId,

        Integer useful) {
}
