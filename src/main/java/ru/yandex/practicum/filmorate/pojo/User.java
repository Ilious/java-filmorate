package ru.yandex.practicum.filmorate.pojo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;

    @NotBlank
    private String email;

    @NotBlank
    private String login;

    @NotBlank
    private String name;

    private LocalDate birthday;
}
