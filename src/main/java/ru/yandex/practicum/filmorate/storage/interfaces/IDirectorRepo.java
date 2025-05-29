package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.dao.DirectorDao;

import java.util.Collection;
import java.util.Optional;

public interface IDirectorRepo {

    Collection<DirectorDao> findAll();

    DirectorDao createDirector(DirectorDao directorDao);

    DirectorDao updateDirector(DirectorDao directorDao);

    Optional<DirectorDao> findDirectorById(Long id);

    void deleteDirector(Long id);
}
