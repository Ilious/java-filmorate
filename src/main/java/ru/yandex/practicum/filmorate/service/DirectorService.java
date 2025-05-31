package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dto.DirectorRecord;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.service.interfaces.IDirectorService;
import ru.yandex.practicum.filmorate.storage.interfaces.IDirectorRepo;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DirectorService implements IDirectorService {

    private final IDirectorRepo directorRepo;

    public DirectorService(IDirectorRepo directorRepo) {
        this.directorRepo = directorRepo;
    }

    @Override
    public DirectorDao postDirector(DirectorRecord directorRecord) {
        DirectorDao directorDao = DirectorMapper.toDirectorDao(directorRecord);
        return directorRepo.createDirector(directorDao);
    }

    @Override
    public DirectorDao putDirector(DirectorRecord directorRecord) {
        if (directorRecord.id() == null) {
            log.warn("putDirector: ID must not be null");
            throw new ValidationException("Director ID is required for update", "id", null);
        }
        DirectorDao directorById = getDirectorById(directorRecord.id());
        DirectorMapper.updateFields(directorById, directorRecord);
        return directorRepo.updateDirector(directorById);
    }

    @Override
    public Collection<DirectorDao> getAllDirectors() {
        Collection<DirectorDao> directors = directorRepo.findAll();
        log.debug("Retrieved {} directors", directors.size());
        return directors;
    }

    @Override
    public DirectorDao getDirectorById(Long id) {
        return directorRepo.findDirectorById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Director not found",
                        "Director",
                        "id",
                        String.valueOf(id)
                ));
    }

    @Override
    public void deleteDirector(Long id) {
        getDirectorById(id);
        directorRepo.deleteDirector(id);
        log.debug("Deleted director with id {}", id);
    }

    @Override
    public void validateIds(List<Long> listIds) {
        Set<Long> allDirectorsIds = getAllDirectors().stream()
                .map(DirectorDao::getId)
                .collect(Collectors.toSet());
        allDirectorsIds.forEach(directorId -> {
            if (!allDirectorsIds.contains(directorId))
                throw new EntityNotFoundException("Director not found", "Director", "id", String.valueOf(directorId));
                });
    }
}
