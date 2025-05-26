package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.component.SearchCriteria;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
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
import ru.yandex.practicum.filmorate.service.interfaces.IDirectorService;
import ru.yandex.practicum.filmorate.service.interfaces.IFilmService;
import ru.yandex.practicum.filmorate.service.interfaces.IGenreService;
import ru.yandex.practicum.filmorate.service.interfaces.IMpaService;
import ru.yandex.practicum.filmorate.storage.interfaces.IFilmRepo;
import ru.yandex.practicum.filmorate.storage.mapper.DirectorMapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService implements IFilmService {

    private final IFilmRepo filmRepo;

    private final IGenreService genreService;

    private final IMpaService mpaService;

    private final IDirectorService directorService;

    @Override
    public FilmDao postFilm(FilmRecord filmRecord) {
        MpaRecord mpaReq = filmRecord.mpa();
        mpaService.validateId(mpaReq.id());
        MpaDao mpa = MpaMapper.toMpaDao(mpaReq);

        List<GenreDao> genreDaos = GenreMapper.toGenresDao(filmRecord.genres());
        List<Long> listGenresIds = genreDaos.stream()
                .map(GenreDao::getId)
                .toList();
        genreService.validateIds(listGenresIds);

        Set<GenreDao> uniqueGenres = new TreeSet<>(
                Comparator.comparing(GenreDao::getId)
        );
        uniqueGenres.addAll(genreDaos);

        List<DirectorDao> directors = DirectorMapper.toDirectorsDaos(filmRecord.directors());
        List<Long> listDirectors = directors.stream()
                .map(DirectorDao::getId)
                .toList();
        directorService.validateIds(listDirectors);

        FilmDao filmDao = FilmMapper.toFilmDao(filmRecord);
        filmDao.setGenres(new ArrayList<>(uniqueGenres));
        filmDao.setDirectors(new ArrayList<>(directors));
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

        if (filmRecord.directors() != null) {
            List<DirectorDao> directors = DirectorMapper.toDirectorsDaos(filmRecord.directors());
            List<Long> listDirectors = directors.stream()
                    .map(DirectorDao::getId)
                    .toList();

            directorService.validateIds(listDirectors);

            dao.setDirectors(directors);
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
    public Collection<FilmDao> getMostLikedFilms(Long count) {
        return filmRepo.findNPopular(count);
    }

    @Override
    public Collection<FilmDao> search(String query, String[] by) {
        SearchCriteria searchObj = defineSearchBy(by, query);

        return filmRepo.findFilmsBySearchQuery(searchObj);
    }

    @Override
    public Collection<FilmDao> getByDirectorId(Long id, String sortBy) {
        directorService.validateIds(List.of(id));

        SortBy sort = SortBy.fromValue(sortBy);

        return switch (sort) {
            case Year -> filmRepo.findByDirectorId(id, new SearchCriteria("ORDER BY f.release_date"));
            case Likes -> getMostLikedFilms(Long.MAX_VALUE).stream()
                    .filter(film -> film.getDirectors()
                            .stream()
                            .anyMatch(f -> f.getId().equals(id))
                    ).toList();
        };
    }

    private SearchCriteria defineSearchBy(String[] by, String query) {
        Set<String> criteria = Arrays.stream(by)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        String queryParam = "%" + query + "%";
        if (criteria.contains("title") && criteria.contains("director"))
            return new SearchCriteria(
                    " WHERE LOWER(f.name) LIKE LOWER(?) OR LOWER(d.name) LIKE LOWER(?)",
                    queryParam, queryParam
            );
        else if (criteria.contains("title"))
                return new SearchCriteria(" WHERE LOWER(f.name) LIKE LOWER(?)", queryParam);
        else if (criteria.contains("director"))
            return new SearchCriteria(" WHERE LOWER(d.name) LIKE LOWER(?)", queryParam);

        throw new ValidationException("Search query is not valid", "search", Arrays.toString(by));
    }
}
