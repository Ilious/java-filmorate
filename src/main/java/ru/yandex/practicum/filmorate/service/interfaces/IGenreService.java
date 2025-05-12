package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.dao.GenreDao;

import java.util.Collection;

public interface IGenreService {

    Collection<GenreDao> getAll();

    GenreDao getById(Long id);

    void validateId(Long id);
}
