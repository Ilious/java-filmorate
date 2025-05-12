package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.dao.GenreDao;

import java.util.Collection;
import java.util.Optional;

public interface IGenreRepo {

    Collection<GenreDao> findAll();

    Optional<GenreDao> findById(Long id);
}
