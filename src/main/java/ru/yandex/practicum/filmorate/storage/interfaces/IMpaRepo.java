package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.dao.MpaDao;

import java.util.Collection;
import java.util.Optional;

public interface IMpaRepo {

    Optional<MpaDao> findById(Long id);

    Collection<MpaDao> findAll();
}
