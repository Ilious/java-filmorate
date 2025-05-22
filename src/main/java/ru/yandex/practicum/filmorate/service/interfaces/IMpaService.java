package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.dao.MpaDao;

import java.util.Collection;

public interface IMpaService {

    Collection<MpaDao> getMpas();

    MpaDao getById(Long id);

    void validateId(Long id);
}
