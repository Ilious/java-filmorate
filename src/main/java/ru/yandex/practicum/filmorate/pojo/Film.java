package ru.yandex.practicum.filmorate.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.yandex.practicum.filmorate.validator.DateBefore;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    private Long id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private Integer duration;

    @Setter(AccessLevel.NONE)
    @Getter(lazy = true)
    @ToString.Exclude
    private final Set<Long> likedId = new HashSet<>();
}
