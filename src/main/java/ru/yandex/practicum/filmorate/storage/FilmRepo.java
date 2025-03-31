package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityExistsException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class FilmRepo implements IFilmRepo {

    private final Map<Long, Film> storage = new HashMap<>();

    @Override
    public Collection<Film> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Film createFilm(Long id, Film film) {
        if (storage.containsKey(film.getId())) {
            log.warn("Film exists by id {}", film.getId());
            throw new EntityExistsException("Film exists by id %d".formatted(film.getId()));
        }
        storage.put(id, film);

        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        return storage.get(id);
    }

    @Override
    public Film updateFilm(Long id, Film film) {
        storage.put(id, film);

        return film;
    }
}
