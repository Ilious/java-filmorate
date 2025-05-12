package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.dao.GenreDao;

import java.util.Collection;
import java.util.List;

public interface IGenreService {

    Collection<GenreDao> getAll();

    GenreDao getById(Long id);

    void validateIds(List<Long> ids);
}
