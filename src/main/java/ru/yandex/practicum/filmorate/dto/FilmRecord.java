package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ru.yandex.practicum.filmorate.validator.DateBefore;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.time.LocalDate;
import java.util.List;

public record FilmRecord(
        @NotNull(groups = Validator.OnUpdate.class) Long id,

        @NotBlank(groups = {Validator.OnUpdate.class, Validator.OnCreate.class}) String name,

        @NotBlank(groups = Validator.OnCreate.class)
        @Size(max = 200, groups = {Validator.OnUpdate.class, Validator.OnCreate.class})
        String description,

        MpaRecord mpa,

        @DateBefore(groups = {Validator.OnUpdate.class, Validator.OnCreate.class})
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate releaseDate,

        @Positive(groups = {Validator.OnUpdate.class, Validator.OnCreate.class})
        int duration,

        List<GenreRecord> genres,

        List<DirectorRecord> directors) {
}
