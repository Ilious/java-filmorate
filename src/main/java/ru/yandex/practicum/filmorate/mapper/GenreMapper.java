package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.enums.Genre;
import ru.yandex.practicum.filmorate.dto.GenreRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GenreMapper {

    public static List<GenreDao> toGenresDao(List<GenreRecord> req) {
        List<GenreDao> genres = new ArrayList<>();
        if (req != null) {
            genres = req
                    .stream()
                    .map(genre -> {
                        GenreDao genreDao = new GenreDao();
                        genreDao.setId(genre.id());
                        genreDao.setName(Genre.fromValue(genre.id()));
                        return genreDao;
                    })
                    .collect(Collectors.toList());
        }
        return genres;
    }
}
