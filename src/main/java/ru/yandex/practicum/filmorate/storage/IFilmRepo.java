package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface IFilmRepo {

    Collection<Film> getAll();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(Long id);
}
