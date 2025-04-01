package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.pojo.FilmRecord;
import ru.yandex.practicum.filmorate.storage.IFilmRepo;

import java.util.Collection;

@Slf4j
@Service
public class FilmService implements IFilmService {

    private final IFilmRepo filmRepo;

    public FilmService(IFilmRepo filmRepo) {
        this.filmRepo = filmRepo;
    }

    @Override
    public Film postFilm(FilmRecord filmRecord) {
        Film film = Film.builder()
                .name(filmRecord.name())
                .description(filmRecord.description())
                .releaseDate(filmRecord.releaseDate())
                .duration(filmRecord.duration())
                .build();

        return filmRepo.createFilm(film);
    }

    @Override
    public Film putFilm(FilmRecord filmRecord) {
        if (filmRecord.id() == null) {
            log.warn("updateFilm: Id is not correct");
            throw new ValidationException("updateFilm: Id is not correct");
        }

        Film filmById = filmRepo.getFilmById(filmRecord.id());

        if (filmRecord.releaseDate() != null)
            filmById.setReleaseDate(filmRecord.releaseDate());

        if (filmRecord.duration() > 0)
            filmById.setDuration(filmRecord.duration());

        if (filmRecord.description() != null)
            filmById.setDescription(filmRecord.description());

        if (filmRecord.name() != null)
            filmById.setName(filmRecord.name());

        return filmById;
    }

    @Override
    public Collection<Film> getAll() {
        Collection<Film> films = filmRepo.getAll();
        log.debug("Get user collection {}", films.size());
        return films;
    }
}
