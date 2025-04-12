package ru.yandex.practicum.filmorate.pojo;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

    @Setter(AccessLevel.NONE)
    @Getter(lazy = true)
    private final Set<Long> friends = new HashSet<>();
}
