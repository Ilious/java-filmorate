package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.pojo.FilmRecord;

import java.util.Collection;

public interface IFilmService {

    Film putFilm(FilmRecord filmRecord);

    Film postFilm(FilmRecord filmRecord);

    Collection<Film> getAll();
}
