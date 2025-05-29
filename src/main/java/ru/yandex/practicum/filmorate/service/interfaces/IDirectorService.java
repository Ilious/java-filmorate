package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dto.DirectorRecord;

import java.util.Collection;
import java.util.List;

public interface IDirectorService {

    DirectorDao postDirector(DirectorRecord directorRecord);

    DirectorDao putDirector(DirectorRecord directorRecord);

    Collection<DirectorDao> getAllDirectors();

    DirectorDao getDirectorById(Long id);

    void deleteDirector(Long id);

    void validateIds(List<Long> listIds);
}
