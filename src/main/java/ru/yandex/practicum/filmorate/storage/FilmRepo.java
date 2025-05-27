package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.interfaces.IFilmRepo;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class FilmRepo extends BaseRepo<FilmDao> implements IFilmRepo {

    private static final String FIND_ALL_QUERY = "SELECT f.id as film_id, " +
            "f.name, " +
            "f.description, " +
            "f.release_date, " +
            "f.duration, " +
            "f.rating_id, " +
            "g.id as genre_id, " +
            "g.name as genre_name, " +
            "d.id as director_id, " +
            "d.name as director_name " +
            "FROM films f " +
            "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
            "LEFT JOIN genres g ON fg.genre_id = g.id " +
            "LEFT JOIN film_directors fd ON f.id = fd.film_id " +
            "LEFT JOIN directors d ON fd.director_id = d.id";


    private static final String FIND_BY_ID_QUERY = "SELECT f.id as film_id, " +
            "f.name, " +
            "f.description, " +
            "f.release_date, " +
            "f.duration, " +
            "f.rating_id, " +
            "g.id as genre_id, " +
            "g.name as genre_name, " +
            "d.id as director_id, " +
            "d.name as director_name " +
            "FROM films f " +
            "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
            "LEFT JOIN genres g ON fg.genre_id = g.id " +
            "LEFT JOIN film_directors fd ON f.id = fd.film_id " +
            "LEFT JOIN directors d ON fd.director_id = d.id " +
            "WHERE f.id = ?";

    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date," +
            " duration, rating_id) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String INSERT_LIKE_QUERY = "INSERT INTO liked_films (film_id, user_id)" +
            "VALUES (?, ?)";

    private static final String DELETE_LIKE_QUERY = "DELETE FROM liked_films WHERE film_id = ? AND user_id = ?";

    private static final String GET_N_POPULAR =
            "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_id  " +
                    "FROM ( " +
                    "SELECT lf.film_id, COUNT(*) as count_likes  " +
                    "FROM liked_films lf " +
                    "GROUP BY lf.film_id " +
                    "ORDER BY count_likes desc " +
                    "LIMIT ? " +
                    ") AS top_films " +
                    "JOIN films f ON f.id = top_films.film_id " +
                    "ORDER BY top_films.count_likes DESC";


    private static final String UPDATE_QUERY = "UPDATE films " +
            "SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?";

    public static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genres (film_id, genre_id) " +
            "VALUES (?, ?)";

    public static final String DELETE_ALL_FILM_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = ?";

    private static final String INSERT_FILM_DIRECTOR_QUERY =
            "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";

    private static final String DELETE_ALL_FILM_DIRECTORS_QUERY =
            "DELETE FROM film_directors WHERE film_id = ?";

    private static final String FIND_FILMS_BY_DIRECTOR_QUERY =
            "SELECT f.id as film_id, f.name, f.description, f.release_date, f.duration, " +
                    "f.rating_id, COUNT(l.user_id) as likes_count, " +
                    "d.id as director_id, d.name as director_name, " +
                    "g.id as genre_id, g.name as genre_name " +
                    "FROM films f " +
                    "JOIN film_directors fd ON f.id = fd.film_id " +
                    "LEFT JOIN liked_films l ON f.id = l.film_id " +
                    "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
                    "LEFT JOIN genres g ON fg.genre_id = g.id " +
                    "LEFT JOIN directors d ON fd.director_id = d.id " +
                    "WHERE fd.director_id = ? " +
                    "GROUP BY f.id, d.id, g.id " +
                    "ORDER BY " +
                    "CASE WHEN ? = 'year' THEN f.release_date end ASC, " +
                    "CASE WHEN ? = 'likes' THEN COUNT(l.user_id) end DESC ";
    private final ResultSetExtractor<List<FilmDao>> extractor;

    private final ResultSetExtractor<FilmDao> singleExtractor;

    public FilmRepo(JdbcTemplate jdbc, RowMapper<FilmDao> mapper,
                    ResultSetExtractor<List<FilmDao>> extractor,
                    ResultSetExtractor<FilmDao> singleExtractor) {
        super(jdbc, mapper);
        this.extractor = extractor;
        this.singleExtractor = singleExtractor;
    }

    @Override
    public Collection<FilmDao> findAll() {
        log.trace("FilmRepo.findAll: findAll");

        return extract(FIND_ALL_QUERY, extractor);
    }

    @Override
    public FilmDao createFilm(FilmDao filmDao) {
        log.trace("FilmRepo.createFilm: film {}", filmDao);

        long id = insert(
                INSERT_QUERY,
                filmDao.getName(),
                filmDao.getDescription(),
                filmDao.getReleaseDate(),
                filmDao.getDuration(),
                filmDao.getMpa().getId()
        );
        filmDao.setId(id);

        addGenresToFilm(filmDao.getId(), filmDao.getGenres());
        addDirectorsToFilm(filmDao.getId(), filmDao.getDirectors());

        return filmDao;
    }

    @Override
    public Optional<FilmDao> findFilmById(Long id) {
        log.trace("UserRepo.findUserById: by id {}", id);

        return Optional.ofNullable(extract(FIND_BY_ID_QUERY, singleExtractor, id));
    }

    @Override
    public FilmDao updateFilm(FilmDao filmDao) {
        log.trace("FilmDao.updateFilm: filmDao {}", filmDao);

        update(
                UPDATE_QUERY,
                filmDao.getName(),
                filmDao.getDescription(),
                filmDao.getReleaseDate(),
                filmDao.getDuration(),
                filmDao.getMpa().getId(),
                filmDao.getId()
        );

        updateFilmGenres(filmDao.getId(), filmDao.getGenres());
        updateFilmDirectors(filmDao.getId(), filmDao.getDirectors());

        return filmDao;
    }

    @Override
    public void setLikeOnFilm(Long filmId, Long userId) {
        insertNoKey(
                INSERT_LIKE_QUERY,
                filmId,
                userId
        );
    }

    @Override
    public void deleteLikeFromFilm(Long filmId, Long userId) {
        delete(
                DELETE_LIKE_QUERY,
                filmId,
                userId
        );
    }

    @Override
    public Collection<FilmDao> findNPopular(Long count) {
        return findMany(
                GET_N_POPULAR,
                count
        );
    }

    public void addGenresToFilm(Long filmId, List<GenreDao> genres) {
        if (genres == null || genres.isEmpty())
            return;

        insertBatch(
                INSERT_FILM_GENRE_QUERY,
                genres,
                (ps, genre) -> {
                    try {
                        ps.setLong(1, filmId);
                        ps.setLong(2, genre.getId());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    public void updateFilmGenres(Long filmId, List<GenreDao> genres) {
        if (genres == null || genres.isEmpty())
            return;

        delete(DELETE_ALL_FILM_GENRES_QUERY, filmId);

        addGenresToFilm(filmId, genres);
    }

    public void addDirectorsToFilm(Long filmId, List<DirectorDao> directors) {
        if (directors == null || directors.isEmpty()) {
            return;
        }

        insertBatch(
                INSERT_FILM_DIRECTOR_QUERY,
                directors,
                (ps, director) -> {
                    try {
                        ps.setLong(1, filmId);
                        ps.setLong(2, director.getId());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    public void updateFilmDirectors(Long filmId, List<DirectorDao> directors) {
        delete(DELETE_ALL_FILM_DIRECTORS_QUERY, filmId);
        addDirectorsToFilm(filmId, directors);
    }

    @Override
    public List<FilmDao> getFilmsByDirector(Long directorId, String sortBy) {
        return extract(FIND_FILMS_BY_DIRECTOR_QUERY, extractor, directorId, sortBy, sortBy);
    }

}
