package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
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

    private static final String SHOW_COMMON_FILMS_QUERY = """
        SELECT f.id AS film_id,
               f.name,
               f.description,
               f.release_date,
               f.duration,
               f.rating_id,
               g.id AS genre_id,
               g.name AS genre_name
        FROM films f
        JOIN liked_films lf1 ON f.id = lf1.film_id AND lf1.user_id = ?
        JOIN liked_films lf2 ON f.id = lf2.film_id AND lf2.user_id = ?
        LEFT JOIN film_genres fg ON f.id = fg.film_id
        LEFT JOIN genres g ON fg.genre_id = g.id
        GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.rating_id, g.id, g.name
        ORDER BY (
            SELECT COUNT(*) FROM liked_films lf WHERE lf.film_id = f.id
        ) DESC
        """;

    private static final String RECOMMENDATION_QUERY = """
        WITH user_likes AS (
            SELECT film_id FROM liked_films WHERE user_id = ?
        ),
        similar_users AS (
            SELECT user_id,
                   COUNT(*) AS common_likes
            FROM liked_films
            WHERE film_id IN (SELECT film_id FROM user_likes)
              AND user_id <> ?
            GROUP BY user_id
            ORDER BY common_likes DESC
            LIMIT 1
        ),
        recommended_films AS (
            SELECT film_id FROM liked_films
            WHERE user_id = (SELECT user_id FROM similar_users)
              AND film_id NOT IN (SELECT film_id FROM user_likes)
        )
        SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_id
        FROM films f
        WHERE f.id IN (SELECT film_id FROM recommended_films)
        """;

    public static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genres (film_id, genre_id) " +
            "VALUES (?, ?)";

    public static final String DELETE_ALL_FILM_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = ?";

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

    @Override
    public Collection<FilmDao> showCommonFilms(Long userId, Long friendId) {
        log.trace("UserRepo.showCommonFilms: userId {}, friendId{}", userId, friendId);

        return extract(SHOW_COMMON_FILMS_QUERY, extractor, userId, friendId);
    }
}
