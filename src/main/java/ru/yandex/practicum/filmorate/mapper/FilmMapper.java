package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dto.FilmRecord;

@RequiredArgsConstructor
public class FilmMapper {

    private final GenreMapper genreMapper;

    public static FilmDao toFilmDao(FilmRecord req) {
        FilmDao filmDao = FilmDao.builder()
                .name(req.name())
                .description(req.description())
                .releaseDate(req.releaseDate())
                .duration(req.duration())
                .build();

        return filmDao;
    }

    public static void updateFields(FilmDao film, FilmRecord req) {
        if (req.mpa() != null)
            film.setMpa(MpaMapper.toMpaDao(req.mpa()));

        if (req.releaseDate() != null)
            film.setReleaseDate(req.releaseDate());

        film.setDuration(req.duration());

        if (req.description() != null)
            film.setDescription(req.description());

        if (req.name() != null)
            film.setName(req.name());
    }
}
