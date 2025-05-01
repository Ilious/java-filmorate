package ru.yandex.practicum.filmorate.pojo;

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

    private String name;

    private String description;

    private LocalDate releaseDate;

    private Integer duration;

    @Setter(AccessLevel.NONE)
    @Getter(lazy = true)
    @ToString.Exclude
    private final Set<Long> likedId = new HashSet<>();
}
