package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.filmorate.validator.Validator;

public record DirectorRecord(
        @NotNull(groups = Validator.OnUpdate.class) Long id,

        @NotBlank(groups = {Validator.OnUpdate.class, Validator.OnCreate.class})
        String name) {
}