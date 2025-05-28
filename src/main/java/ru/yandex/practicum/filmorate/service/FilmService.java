package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dto.FilmRecord;
import ru.yandex.practicum.filmorate.dto.GenreRecord;
import ru.yandex.practicum.filmorate.dto.MpaRecord;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.service.interfaces.IFilmService;
import ru.yandex.practicum.filmorate.service.interfaces.IGenreService;
import ru.yandex.practicum.filmorate.service.interfaces.IMpaService;
import ru.yandex.practicum.filmorate.storage.interfaces.IFilmRepo;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService implements IFilmService {

    private final IFilmRepo filmRepo;

    private final IGenreService genreService;

    private final IMpaService mpaService;

    @Override
    public FilmDao postFilm(FilmRecord filmRecord) {
        MpaRecord mpaReq = filmRecord.mpa();
        mpaService.validateId(mpaReq.id());
        MpaDao mpa = MpaMapper.toMpaDao(mpaReq);

        List<GenreDao> dao = GenreMapper.toGenresDao(filmRecord.genres());
        List<Long> list = dao.stream()
                .map(GenreDao::getId)
                .toList();
        genreService.validateIds(list);

        Set<GenreDao> uniqueGenres = new TreeSet<>(
                Comparator.comparing(GenreDao::getId)
        );
        uniqueGenres.addAll(dao);

        FilmDao filmDao = FilmMapper.toFilmDao(filmRecord);
        filmDao.setGenres(new ArrayList<>(uniqueGenres));
        filmDao.setMpa(mpa);

        return filmRepo.createFilm(filmDao);
    }

    @Override
    public FilmDao putFilm(FilmRecord filmRecord) {
        if (filmRecord.id() == null) {
            log.warn("updateFilm: Id is not correct");
            throw new ValidationException("updateFilm: is not correct", "id", null);
        }

        FilmDao dao = getById(filmRecord.id());
        log.debug("updateFilm {} {}", filmRecord.id(), filmRecord.name());

        if (filmRecord.genres() != null) {
            List<Long> ids = filmRecord.genres()
                    .stream()
                    .map(GenreRecord::id)
                    .toList();
            List<GenreDao> genresDao = GenreMapper.toGenresDao(filmRecord.genres());

            genreService.validateIds(ids);

            dao.setGenres(genresDao);
        }

        MpaRecord mpaReq = filmRecord.mpa();
        if (mpaReq != null) {
            mpaService.validateId(mpaReq.id());
            dao.setMpa(MpaMapper.toMpaDao(mpaReq));
        }

        FilmMapper.updateFields(dao, filmRecord);

        return filmRepo.updateFilm(dao);
    }

    @Override
    public FilmDao getById(Long filmId) {
        return filmRepo.findFilmById(filmId)
                .orElseThrow(() -> new EntityNotFoundException(
                                "Entity Film not found", "Film", "Id", String.valueOf(filmId)
                        )
                );
    }

    @Override
    public Collection<FilmDao> getAll() {
        Collection<FilmDao> filmDaos = filmRepo.findAll();
        log.debug("Get user collection {}", filmDaos.size());
        return filmDaos;
    }

    @Override
    public void setLikeOnFilm(Long userId, Long filmId) {
        filmRepo.setLikeOnFilm(filmId, userId);
    }

    @Override
    public void deleteLikeOnFilm(Long userId, Long filmId) {
        filmRepo.deleteLikeFromFilm(filmId, userId);
    }

    @Override
    public Collection<FilmDao> getMostLikedFilms(Long count, Long genreId, Integer year) {
        return filmRepo.findNPopular(count, genreId, year);
    }

    @Override
    public void deleteFilm(Long filmId) {
        getById(filmId);

        filmRepo.deleteFilm(filmId);
    }
}
