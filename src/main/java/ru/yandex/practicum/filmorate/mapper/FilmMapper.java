package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dto.DirectorRecord;
import ru.yandex.practicum.filmorate.dto.FilmRecord;

import java.util.List;
import java.util.stream.Collectors;

public class FilmMapper {

    public static FilmDao toFilmDao(FilmRecord req) {
        return FilmDao.builder()
                .name(req.name())
                .description(req.description())
                .releaseDate(req.releaseDate())
                .duration(req.duration())
                .directors(convertDirectorRecords(req.directors()))
                .build();
    }

    public static void updateFields(FilmDao film, FilmRecord req) {
        if (req.mpa() != null) {
            film.setMpa(MpaMapper.toMpaDao(req.mpa()));
        }

        if (req.releaseDate() != null) {
            film.setReleaseDate(req.releaseDate());
        }

        film.setDuration(req.duration());

        if (req.description() != null) {
            film.setDescription(req.description());
        }

        if (req.name() != null) {
            film.setName(req.name());
        }

        if (req.directors() != null) {
            film.setDirectors(convertDirectorRecords(req.directors()));
        }
    }

    private static List<DirectorDao> convertDirectorRecords(List<DirectorRecord> directorRecords) {
        if (directorRecords == null) {
            return List.of();
        }

        return directorRecords.stream()
                .map(DirectorMapper::toDirectorDao)
                .collect(Collectors.toList());
    }
}