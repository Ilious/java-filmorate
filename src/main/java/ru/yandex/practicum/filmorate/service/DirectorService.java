package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dto.DirectorRecord;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.service.interfaces.IDirectorService;
import ru.yandex.practicum.filmorate.storage.interfaces.IDirectorRepo;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class DirectorService implements IDirectorService {

     private final IDirectorRepo directorRepo;

    public DirectorService(IDirectorRepo directorRepo) {
        this.directorRepo = directorRepo;
    }

    @Override
    public void deleteDirector(Long id) {
        log.debug("deleteDirector: by id {}", id);

        directorRepo.deleteById(id);
    }

    @Override
    public DirectorDao getById(Long id) {
        return directorRepo.findDirectorById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                                "Entity Director not found", "Director", "Id", String.valueOf(id)
                        )
                );
    }

    @Override
    public Collection<DirectorDao> getDirectors() {
        Collection<DirectorDao> directorDaos = directorRepo.findAll();
        log.debug("Get user collection {}", directorDaos.size());

        return directorDaos;
    }

    @Override
    public DirectorDao postDirector(DirectorRecord req) {
        DirectorDao directorDao = DirectorMapper.toDirectorDao(req);

        return directorRepo.createDirector(directorDao);
    }

    @Override
    public DirectorDao putDirector(DirectorRecord req) {
        DirectorDao directorDao = getById(req.id());
        DirectorMapper.updateFields(directorDao, req);

        log.debug("putDirector {} {}", req.id(), req.name());

        return directorRepo.updateDirector(directorDao);
    }

    @Override
    public void validateIds(List<Long> listIds) {
        listIds.forEach(this::getById);
    }
}
