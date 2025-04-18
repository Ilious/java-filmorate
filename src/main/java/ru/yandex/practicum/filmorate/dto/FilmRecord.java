package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ru.yandex.practicum.filmorate.validator.DateBefore;

import java.time.LocalDate;

public record FilmRecord(Long id,

                         @NotBlank String name,

                         @NotBlank @Size(max = 200) String description,

                         @DateBefore @JsonFormat(pattern = "yyyy-MM-dd") LocalDate releaseDate,

                         @Positive int duration) {
}
