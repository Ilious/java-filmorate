package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.pojo.Film;
import ru.yandex.practicum.filmorate.dto.FilmRecord;

import java.util.Collection;

public interface IFilmService {

    Film putFilm(FilmRecord filmRecord);

    Film postFilm(FilmRecord filmRecord);

    Collection<Film> getAll();

    Film setLikeOnFilm(Long userId, Long filmId);

    Film deleteLikeOnFilm(Long userId, Long filmId);

    Collection<Film> getMostLikedFilms(Long count);
}
