package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.pojo.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.IFilmRepo;

import java.util.*;

@Slf4j
@Repository
public class FilmRepo implements IFilmRepo {

    private final Map<Long, Film> storage = new HashMap<>();

    private Long idx = 0L;

    private Long updIdx() {
        return ++idx;
    }

    @Override
    public Collection<Film> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(updIdx());
        log.debug("postFilm {} {}", film.getId(), film.getName());

        storage.put(film.getId(), film);

        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        if (Objects.isNull(storage.get(id))) {
            String errMessage = String.format("Film not found by id %d", id);
            log.warn(errMessage);
            throw new EntityNotFoundException(errMessage, "Film", String.valueOf(id));
        }

        return storage.get(id);
    }

    @Override
    public Film updateFilm(Film film) {
        storage.put(film.getId(), film);

        return film;
    }
}
