package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilmDao {

    private Long id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private Integer duration;

    private MpaDao mpa;

    private List<GenreDao> genres;
}
