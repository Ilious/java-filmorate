package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface IFilmRepo {

    Collection<Film> getAll();

    Film createFilm(Long id, Film film);

    Film updateFilm(Long id, Film film);

    Film getFilmById(Long id);
}
