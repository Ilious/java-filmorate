package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.pojo.Film;

import java.util.Collection;

public interface IFilmRepo {

    Collection<Film> getAll();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(Long id);
}
