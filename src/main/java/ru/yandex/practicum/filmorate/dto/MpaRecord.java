package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;

public record MpaRecord(
        @NotNull Long id
) {
}
