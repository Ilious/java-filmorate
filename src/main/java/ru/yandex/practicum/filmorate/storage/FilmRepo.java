package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.interfaces.IFilmRepo;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;

import java.sql.SQLException;
import java.util.ArrayList;
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
            "g.name as genre_name " +
            "FROM films f " +
            "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
            "LEFT JOIN genres g ON fg.genre_id = g.id";

    private static final String FIND_BY_ID_QUERY = "SELECT f.id as film_id, " +
            "f.name, " +
            "f.description, " +
            "f.release_date, " +
            "f.duration, " +
            "f.rating_id, " +
            "g.id as genre_id, " +
            "g.name as genre_name " +
            "FROM films f " +
            "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
            "LEFT JOIN genres g ON fg.genre_id = g.id " +
            "WHERE f.id = ?";

    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date," +
            " duration, rating_id) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String INSERT_LIKE_QUERY = "INSERT INTO liked_films (film_id, user_id)" +
            "VALUES (?, ?)";

    private static final String DELETE_LIKE_QUERY = "DELETE FROM liked_films WHERE film_id = ? AND user_id = ?";


    private static final String UPDATE_QUERY = "UPDATE films " +
            "SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?";

    public static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genres (film_id, genre_id) " +
            "VALUES (?, ?)";

    public static final String DELETE_ALL_FILM_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = ?";

    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE id = ?";

    public static final String GET_RECOMMENDATION_QUERY = "SELECT " +
            "f.id AS film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, " +
            "g.id AS genre_id, " +
            "g.name AS genre_name, " +
            "COUNT(lf2.user_id) AS recommendation_weight " +
            "FROM films f " +
            "LEFT JOIN film_genres fg ON fg.film_id = f.id " +
            "LEFT JOIN genres g ON g.id = fg.genre_id " +
            "JOIN liked_films lf2 ON lf2.film_id = f.id " +
            "WHERE lf2.user_id IN ( " +
            "SELECT DISTINCT lf.user_id " +
            "FROM liked_films lf " +
            "WHERE lf.user_id != ? " +
            "AND lf.film_id IN (SELECT film_id FROM liked_films WHERE user_id = ?)) " +
            "AND f.id NOT IN (SELECT film_id FROM liked_films WHERE user_id = ?) " +
            "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.rating_id, g.id, g.name " +
            "ORDER BY recommendation_weight DESC;";

    public static final String LOAD_GENRES_FOR_FILM = "SELECT distinct g.id, g.name " +
            "FROM genres g JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id = ?";

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
        log.trace("FilmRepo.createFilm: user {}", filmDao);

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

    private List<GenreDao> loadGenresForFilm(Long filmId) {
        return jdbc.query(LOAD_GENRES_FOR_FILM, new GenreMapper(), filmId);
    }

    @Override
    public Collection<FilmDao> findNPopular(Long count, Long genreId, Integer year) {
        StringBuilder sqlBuilder = new StringBuilder();
        List<Object> parameters = new ArrayList<>();

        sqlBuilder.append(
                """
                        SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_id
                        FROM films f
                        LEFT JOIN (
                        SELECT lf.film_id, COUNT(*) as count_likes
                        FROM liked_films lf
                        JOIN films f ON lf.film_id = f.id
                        WHERE 1=1\s"""
        );

        if (genreId != null) {
            sqlBuilder.append("AND EXISTS (SELECT * FROM film_genres fg WHERE fg.film_id = f.id AND fg.genre_id = ?) ");
            parameters.add(genreId);
        }

        if (year != null) {
            sqlBuilder.append("AND YEAR(f.release_date) = ? ");
            parameters.add(year);
        }

        sqlBuilder.append(
                """
                        GROUP BY lf.film_id
                        ) AS top_films ON f.id = top_films.film_id
                        
                        ORDER BY COALESCE(top_films.count_likes, 0) DESC
                        LIMIT ?"""
        );

        parameters.add(count);
        List<FilmDao> films = findMany(sqlBuilder.toString(), parameters.toArray());
        films.forEach(film -> {
            film.setGenres(loadGenresForFilm(film.getId()));
        });
        return films;
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

    @Override
    public void deleteFilm(Long filmId) {
        log.trace("FilmRepo.deleteFilm: by filmId {}", filmId);

        delete(
                DELETE_FILM_QUERY, filmId
        );
    }

    @Override
    public Collection<FilmDao> getRecommendations(Long userId) {
        return extract(GET_RECOMMENDATION_QUERY, extractor, userId, userId, userId);
    }
}
