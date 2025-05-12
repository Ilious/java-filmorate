package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.time.LocalDate;

public record UserRecord(

        @NotNull(groups = Validator.OnUpdate.class) Long id,

        @NotBlank @Email String email,

        @NotBlank @Pattern(regexp = "\\S+") String login,

        String name,

        @Past(groups = {Validator.OnCreate.class, Validator.OnUpdate.class})
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthday) {

}
