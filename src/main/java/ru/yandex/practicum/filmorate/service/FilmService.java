package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.PropertySource;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.pojo.Film;
import ru.yandex.practicum.filmorate.dto.FilmRecord;
import ru.yandex.practicum.filmorate.pojo.User;
import ru.yandex.practicum.filmorate.service.interfaces.IFilmService;
import ru.yandex.practicum.filmorate.storage.UserRepo;
import ru.yandex.practicum.filmorate.storage.interfaces.IFilmRepo;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService implements IFilmService {

    private final IFilmRepo filmRepo;
    private final UserRepo userRepo;

    public FilmService(IFilmRepo filmRepo, UserRepo userRepo) {
        this.filmRepo = filmRepo;
        this.userRepo = userRepo;
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

    @Override
    public Film setLikeOnFilm(Long userId, Long filmId) {
        Film filmById = filmRepo.getFilmById(filmId);

        User userById = userRepo.getUserById(userId);

        filmById.getLikedId().add(userById.getId());
        return filmById;
    }

    @Override
    public Film deleteLikeOnFilm(Long userId, Long filmId) {
        Film filmById = filmRepo.getFilmById(filmId);

        filmById.getLikedId().remove(userId);
        return filmById;
    }

    @Override
    public Collection<Film> getMostLikedFilms() {
        return filmRepo.getAll()
                        .stream()
                        .sorted(Comparator.comparingInt(film -> film.getLikedId().size()))
                        .limit(10)
                        .collect(Collectors.toList());
    }
}
