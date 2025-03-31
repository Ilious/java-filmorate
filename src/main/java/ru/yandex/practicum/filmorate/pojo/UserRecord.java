package ru.yandex.practicum.filmorate.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.NonNull;

import java.time.LocalDate;

public record UserRecord(

        Long id,

        @NotBlank @Email String email,

        @NotBlank @Pattern(regexp = "\\S+") String login,

        String name,

        @Past @JsonFormat(pattern = "yyyy-MM-dd")

        LocalDate birthday) {

}
