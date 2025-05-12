package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.service.interfaces.IGenreService;
import ru.yandex.practicum.filmorate.storage.interfaces.IGenreRepo;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GenreService implements IGenreService {

    private final IGenreRepo genreRepo;

    public GenreService(IGenreRepo genreRepo) {
        this.genreRepo = genreRepo;
    }

    @Override
    public GenreDao getById(Long id) {
        return genreRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                                "Entity Genre not found", "Genre", "id", String.valueOf(id)
                        )
                );
    }

    @Override
    public Collection<GenreDao> getAll() {
        return genreRepo.findAll();
    }

    @Override
    public void validateIds(List<Long> ids) {
        Set<Long> existingIds = new HashSet<>(getAll()).stream()
                .map(GenreDao::getId)
                .collect(Collectors.toSet());
        ids.forEach(genre -> {
                    if (!existingIds.contains(genre))
                        throw new EntityNotFoundException(
                                "Entity Genre not found", "Genre", "genre", String.valueOf(genre)
                        );
                }
        );
    }
}
