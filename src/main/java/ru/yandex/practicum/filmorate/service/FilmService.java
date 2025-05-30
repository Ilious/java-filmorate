package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.component.SearchCriteria;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.dto.FeedRecord;
import ru.yandex.practicum.filmorate.dto.FilmRecord;
import ru.yandex.practicum.filmorate.dto.GenreRecord;
import ru.yandex.practicum.filmorate.dto.MpaRecord;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.service.enums.EntityType;
import ru.yandex.practicum.filmorate.service.enums.Operation;
import ru.yandex.practicum.filmorate.service.interfaces.IFeedService;
import ru.yandex.practicum.filmorate.service.interfaces.IDirectorService;
import ru.yandex.practicum.filmorate.service.interfaces.IFilmService;
import ru.yandex.practicum.filmorate.service.interfaces.IGenreService;
import ru.yandex.practicum.filmorate.service.interfaces.IMpaService;
import ru.yandex.practicum.filmorate.storage.interfaces.IFilmRepo;
import ru.yandex.practicum.filmorate.storage.interfaces.IUserRepo;
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

    private final IFeedService feedService;

    private final IDirectorService directorService;

    private final IUserRepo userRepo;

    @Override
    public FilmDao postFilm(FilmRecord filmRecord) {
        MpaRecord mpaReq = filmRecord.mpa();
        mpaService.validateId(mpaReq.id());
        MpaDao mpa = MpaMapper.toMpaDao(mpaReq);

        Set<GenreDao> uniqueGenres = new HashSet<>(GenreMapper.toGenresDao(filmRecord.genres()));
        List<Long> listGenresIds = uniqueGenres.stream()
                .map(GenreDao::getId)
                .toList();
        genreService.validateIds(listGenresIds);

        List<DirectorDao> directors = DirectorMapper.toDirectorsDaos(filmRecord.directors());
        List<Long> listDirectors = directors.stream()
                .map(DirectorDao::getId)
                .sorted()
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

        dao.setGenres(new ArrayList<>());
        if (filmRecord.genres() != null) {
            List<Long> ids = filmRecord.genres()
                    .stream()
                    .map(GenreRecord::id).distinct()
                    .collect(Collectors.toList());
            Set<GenreDao> genresDao = new HashSet<>(GenreMapper.toGenresDao(filmRecord.genres()));

            genreService.validateIds(ids);

            List<GenreDao> genres = new ArrayList<>(genresDao);
            Collections.sort(genres, Comparator.comparing(GenreDao::getId));
            dao.setGenres(genres);
        }

        dao.setDirectors(new ArrayList<>());
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
        getById(filmId);
        validateUserId(userId);
        filmRepo.setLikeOnFilm(filmId, userId);

        feedService.postFeed(new FeedRecord(userId, filmId, EntityType.LIKE, Operation.ADD));
    }

    @Override
    public void deleteLikeOnFilm(Long userId, Long filmId) {
        getById(filmId);
        validateUserId(userId);
        filmRepo.deleteLikeFromFilm(filmId, userId);

        feedService.postFeed(new FeedRecord(userId, filmId, EntityType.LIKE, Operation.REMOVE));
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


    public List<FilmDao> getFilmsByDirector(Long directorId, String sortBy) {
        directorService.getDirectorById(directorId);
        return filmRepo.getFilmsByDirector(directorId, sortBy);
    }

    @Override

    public Collection<FilmDao> getRecommendations(Long userId) {
        return filmRepo.getRecommendations(userId);
    }

    @Override
    public Collection<FilmDao> showCommonFilms(Long userId, Long friendId) {
        return filmRepo.showCommonFilms(userId, friendId);
    }

    @Override
    public Collection<FilmDao> search(String query, String[] by) {
        SearchCriteria searchObj = defineSearchBy(by, query);

        return filmRepo.findFilmsBySearchQuery(searchObj);
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

    private void validateUserId(Long id) {
        userRepo.findUserById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                                "Entity user not found", "User", "id", String.valueOf(id)
                        )
                );
    }
}
