package ru.yandex.practicum.filmorate.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 200)
    private String description;

    private LocalDate releaseDate;

    @Positive
    private Integer duration;

    @Setter(AccessLevel.NONE)
    @Getter(lazy = true)
    @ToString.Exclude
    private final Set<Long> likedId = new HashSet<>();
}
