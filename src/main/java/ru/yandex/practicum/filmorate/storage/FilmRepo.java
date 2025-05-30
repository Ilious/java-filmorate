package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.component.SearchCriteria;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.HasId;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.storage.interfaces.IFilmRepo;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;

import java.sql.SQLException;
import java.util.*;

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

    private static final String FIND_BY_SEARCH_QUERY = "SELECT f.id as film_id, " +
            "f.name, " +
            "d.id as director_id, " +
            "d.name as director_name, " +
            "f.description, " +
            "f.release_date, " +
            "f.duration, " +
            "f.rating_id, " +
            "g.id as genre_id, " +
            "g.name as genre_name " +
            "FROM films f " +
            "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
            "LEFT JOIN genres g ON fg.genre_id = g.id " +
            "LEFT JOIN film_directors fd ON f.id = fd.film_id " +
            "LEFT JOIN directors d ON d.id = fd.director_id";

    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date," +
            " duration, rating_id) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String INSERT_LIKE_QUERY = "INSERT INTO liked_films (film_id, user_id)" +
            "VALUES (?, ?)";

    private static final String DELETE_LIKE_QUERY = "DELETE FROM liked_films WHERE film_id = ? AND user_id = ?";


    private static final String UPDATE_QUERY = "UPDATE films " +
            "SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
            "WHERE id = ?";

    private static final String SHOW_COMMON_FILMS_QUERY = "SELECT f.id AS film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, g.id AS genre_id, " +
            "g.name AS genre_name, d.id AS director_id, d.name AS director_name " +
            "FROM films f " +
            "JOIN liked_films lf1 ON f.id = lf1.film_id AND lf1.user_id =? " +
            "JOIN liked_films lf2 ON f.id = lf2.film_id AND lf2.user_id =? " +
            "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
            "LEFT JOIN genres g ON fg.genre_id = g.id " +
            "LEFT JOIN film_directors fd ON f.id = fd.film_id " +
            "LEFT JOIN directors d ON fd.director_id = d.id; ";

    public static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genres (film_id, genre_id) " +
            "VALUES (?, ?)";

    public static final String DELETE_ALL_FILM_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = ?";


    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE id = ?";

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

    public static final String GET_RECOMMENDATION_QUERY = "SELECT " +
            "f.id AS film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, " +
            "g.id AS genre_id, " +
            "g.name AS genre_name, " +
            "d.id as director_id, d.name as director_name, " +
            "COUNT(lf2.user_id) AS recommendation_weight " +
            "FROM films f " +
            "LEFT JOIN film_genres fg ON fg.film_id = f.id " +
            "LEFT JOIN genres g ON g.id = fg.genre_id " +
            "JOIN liked_films lf2 ON lf2.film_id = f.id " +
            "LEFT JOIN film_directors fd ON f.id = fd.film_id " +
            "LEFT JOIN directors d ON fd.director_id = d.id " +
            "WHERE lf2.user_id IN ( " +
            "SELECT DISTINCT lf.user_id " +
            "FROM liked_films lf " +
            "WHERE lf.user_id != ? " +
            "AND lf.film_id IN (SELECT film_id FROM liked_films WHERE user_id = ?)) " +
            "AND f.id NOT IN (SELECT film_id FROM liked_films WHERE user_id = ?) " +
            "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.rating_id, g.id, g.name, fd.director_id " +
            "ORDER BY recommendation_weight DESC;";

    public static final String LOAD_GENRES_FOR_FILM = "SELECT distinct g.id, g.name " +
            "FROM genres g JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id = ?";

    public static final String GENRES_ISN_T_ADDED = "Genres isn't added";

    public static final String DIRECTORS_ISN_T_ADDED = "Directors isn't added";

    private final ResultSetExtractor<List<FilmDao>> extractor;

    private final ResultSetExtractor<FilmDao> singleExtractor;

    public FilmRepo(JdbcTemplate jdbc,
                    ResultSetExtractor<List<FilmDao>> extractor,
                    ResultSetExtractor<FilmDao> singleExtractor) {
        super(jdbc);
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

        addSubEntitiesToFilm(
                filmDao.getId(), filmDao.getGenres(), INSERT_FILM_GENRE_QUERY, GENRES_ISN_T_ADDED
        );

        addSubEntitiesToFilm(
                filmDao.getId(), filmDao.getDirectors(), INSERT_FILM_DIRECTOR_QUERY, DIRECTORS_ISN_T_ADDED
        );

        return filmDao;
    }

    @Override
    public Optional<FilmDao> findFilmById(Long id) {
        log.trace("UserRepo.findUserById: by id {}", id);

        return Optional.ofNullable(extract(FIND_BY_ID_QUERY, singleExtractor, id));
    }

    @Override
    public Collection<FilmDao> findFilmsBySearchQuery(SearchCriteria searchObj) {
        log.trace("UserRepo.findFilmBySearchQuery: by search query {}", searchObj);

        return extract(
                FIND_BY_SEARCH_QUERY + searchObj.getQuery(),
                extractor,
                searchObj.getParams()
        );
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

        if (filmDao.getGenres().isEmpty()) {
            int count = jdbc.queryForObject("SELECT COUNT(genre_id) FROM film_genres WHERE film_id = ?",
                    Integer.class, filmDao.getId());

                if (count > 0) {
                    update(
                            DELETE_ALL_FILM_GENRES_QUERY, filmDao.getId()
                    );
                }

        } else {
           updateSubEntities(
                    filmDao.getId(), filmDao.getGenres(),
                    DELETE_ALL_FILM_GENRES_QUERY, INSERT_FILM_GENRE_QUERY, GENRES_ISN_T_ADDED
            );
        }

        updateSubEntities(
                filmDao.getId(), filmDao.getDirectors(),
                DELETE_ALL_FILM_DIRECTORS_QUERY, INSERT_FILM_DIRECTOR_QUERY, DIRECTORS_ISN_T_ADDED
        );

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

        sqlBuilder.append("""
                    SELECT f.id as film_id, f.name, f.description, f.release_date, f.duration, f.rating_id,
                           d.id AS director_id,
                           d.name AS director_name,
                           g.id AS genre_id,
                           g.name AS genre_name
                    FROM films f
                    LEFT JOIN (
                        SELECT lf.film_id, COUNT(*) AS count_likes
                        FROM liked_films lf
                        WHERE 1=1
                """);

        if (genreId != null) {
            sqlBuilder.append("""
                            AND EXISTS (
                                SELECT 1 FROM film_genres fg
                                WHERE fg.film_id = lf.film_id AND fg.genre_id = ?
                            )
                    """);
            parameters.add(genreId);
        }

        if (year != null) {
            sqlBuilder.append("""
                            AND EXISTS (
                                SELECT 1 FROM films f2
                                WHERE f2.id = lf.film_id AND YEAR(f2.release_date) = ?
                            )
                    """);
            parameters.add(year);
        }

        sqlBuilder.append("""
                        GROUP BY lf.film_id
                    ) AS top_films ON f.id = top_films.film_id
                    LEFT JOIN film_genres fg ON f.id = fg.film_id
                    LEFT JOIN genres g ON fg.genre_id = g.id
                    LEFT JOIN film_directors fd ON f.id = fd.film_id
                    LEFT JOIN directors d ON d.id = fd.director_id
                    ORDER BY COALESCE(top_films.count_likes, 0) DESC
                    LIMIT ?
                """);

        parameters.add(count);
        List<FilmDao> films = extract(sqlBuilder.toString(), extractor, parameters.toArray());
        films.forEach(film -> {
            film.setGenres(loadGenresForFilm(film.getId()));
        });
        return films;
    }

    private <T extends HasId> void addSubEntitiesToFilm(Long filmId, List<T> entities, String query, String errMsg) {
        if (entities == null || entities.isEmpty())
            return;

        insertBatch(
                query,
                entities,
                (ps, genre) -> {
                    try {
                        ps.setLong(1, filmId);
                        ps.setLong(2, genre.getId());
                    } catch (SQLException e) {
                        throw new InternalServerException(errMsg);
                    }
                }
        );
    }

    private <T extends HasId> void updateSubEntities(
            Long filmId, List<T> entities, String delQuery, String insertQuery, String errMsg
    ) {
        if (entities == null || entities.isEmpty())
            return;

        delete(delQuery, filmId);

        addSubEntitiesToFilm(filmId, entities, insertQuery, errMsg);
    }


    @Override
    public void deleteFilm(Long filmId) {
        log.trace("FilmRepo.deleteFilm: by filmId {}", filmId);

        delete(
                DELETE_FILM_QUERY, filmId
        );
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

    @Override
    public List<FilmDao> getFilmsByDirector(Long directorId, String sortBy) {
        return extract(FIND_FILMS_BY_DIRECTOR_QUERY, extractor, directorId, sortBy, sortBy);
    }

    @Override
    public Collection<FilmDao> getRecommendations(Long userId) {
        return extract(GET_RECOMMENDATION_QUERY, extractor, userId, userId, userId);
    }

    @Override
    public Collection<FilmDao> showCommonFilms(Long userId, Long friendId) {
        log.trace("UserRepo.showCommonFilms: userId {}, friendId{}", userId, friendId);

        return extract(SHOW_COMMON_FILMS_QUERY, extractor, userId, friendId);
    }
}
