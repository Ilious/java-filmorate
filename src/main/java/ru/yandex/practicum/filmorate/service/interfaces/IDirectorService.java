package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dto.DirectorRecord;

import java.util.Collection;
import java.util.List;

public interface IDirectorService {

    DirectorDao getById(Long id);

    Collection<DirectorDao> getDirectors();

    DirectorDao postDirector(DirectorRecord req);

    DirectorDao putDirector(DirectorRecord req);

    void deleteDirector(Long id);

    void validateIds(List<Long> listIds);
}
