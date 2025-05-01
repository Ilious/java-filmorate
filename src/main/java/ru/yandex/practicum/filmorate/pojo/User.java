package ru.yandex.practicum.filmorate.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
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

    private String email;

    private String login;

    private String name;

    private LocalDate birthday;

    @Setter(AccessLevel.NONE)
    @Getter(lazy = true)
    private final Set<Long> friends = new HashSet<>();
}
