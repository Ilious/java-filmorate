package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dao.enums.AgeRating;
import ru.yandex.practicum.filmorate.dao.enums.Genre;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.storage.mapper.FilmExtractor;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.mapper.SingleFilmExtractor;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmMapper.class, UserMapper.class})
class FilmRepoTest {

    private FilmRepo filmRepo;

    @Autowired
    private RowMapper<FilmDao> filmDaoRowMapper;

    @Autowired
    private RowMapper<UserDao> userDaoRowMapper;

    @Autowired
    private JdbcTemplate jdbc;

    private UserRepo userRepo;

    @BeforeEach
    void init() {
        filmRepo = new FilmRepo(
                jdbc,
                filmDaoRowMapper,
                new FilmExtractor(),
                new SingleFilmExtractor()
        );
        userRepo = new UserRepo(
                jdbc,
                userDaoRowMapper
        );
    }

    @Test
    void findAllTest() {
        MpaDao mpaDao = new MpaDao(1L, AgeRating.fromValue("G"));

        FilmDao film = FilmDao.builder()
                .name("film")
                .description("super-film")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(new ArrayList<>())
                .mpa(mpaDao)
                .build();
        FilmDao film2 = FilmDao.builder()
                .name("film2")
                .description("super-film2")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(new ArrayList<>())
                .mpa(mpaDao)
                .build();
        FilmDao film3 = FilmDao.builder()
                .name("film3")
                .description("super-film3")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(new ArrayList<>())
                .mpa(mpaDao)
                .build();

        filmRepo.createFilm(film);
        filmRepo.createFilm(film2);
        filmRepo.createFilm(film3);
        List<FilmDao> list = new ArrayList<>(filmRepo.findAll());

        assertAll(() -> {
            assertFalse(list.isEmpty());
            assertEquals(3, list.size());
        });
    }

    @Test
    void createFilmTest() {
        MpaDao mpaDao = new MpaDao(1L, AgeRating.fromValue("G"));

        FilmDao film = FilmDao.builder()
                .name("film")
                .description("super-film")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(new ArrayList<>())
                .mpa(mpaDao)
                .build();

        assertDoesNotThrow(() -> filmRepo.createFilm(film).getId());
    }

    @Test
    void findFilmByIdTest() {
        MpaDao mpaDao = new MpaDao(1L, AgeRating.fromValue("G"));

        FilmDao film = FilmDao.builder()
                .name("film")
                .description("super-film")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(new ArrayList<>())
                .mpa(mpaDao)
                .build();
        Long id = filmRepo.createFilm(film).getId();
        Optional<FilmDao> filmById = filmRepo.findFilmById(id);

        assertAll(() -> {
            assertTrue(filmById.isPresent());
            assertEquals(film, filmById.get());
        });
    }

    @Test
    void updateFilmTest() {
        MpaDao mpaDao = new MpaDao(1L, AgeRating.fromValue("G"));

        FilmDao film = FilmDao.builder()
                .name("film")
                .description("super-film")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(new ArrayList<>())
                .mpa(mpaDao)
                .build();
        ArrayList<GenreDao> genres = new ArrayList<>();
        genres.add(new GenreDao(1L, Genre.COMEDY));
        FilmDao upd = FilmDao.builder()
                .name("filmUpd")
                .description("super-filmUpd")
                .releaseDate(LocalDate.now().plusDays(2))
                .duration(130)
                .genres(genres)
                .mpa(new MpaDao(2L, AgeRating.fromValue("PG")))
                .build();

        Long id = filmRepo.createFilm(film).getId();
        upd.setId(id);
        FilmDao filmDao = filmRepo.updateFilm(upd);

        assertAll(() -> {
            assertEquals(upd.getName(), filmDao.getName());
            assertEquals(upd.getDescription(), filmDao.getDescription());
            assertEquals(upd.getReleaseDate(), filmDao.getReleaseDate());
            assertEquals(upd.getDuration(), filmDao.getDuration());
            assertEquals(upd.getGenres(), filmDao.getGenres());
            assertEquals(upd.getMpa(), filmDao.getMpa());
        });
    }

    @Test
    void setLikeOnFilmTest() {
        MpaDao mpaDao = new MpaDao(1L, AgeRating.fromValue("G"));
        GenreDao genreDao = new GenreDao(1L, Genre.COMEDY);
        ArrayList<GenreDao> genre = new ArrayList<>();
        genre.add(genreDao);
        FilmDao film = FilmDao.builder()
                .name("film1")
                .description("super-film1")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(genre)
                .mpa(mpaDao)
                .build();
        FilmDao film2 = FilmDao.builder()
                .name("film2")
                .description("super-film2")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(genre)
                .mpa(mpaDao)
                .build();

        filmRepo.createFilm(film);
        filmRepo.createFilm(film2);

        UserDao user = UserDao.builder()
                .email("email@email.ru")
                .login("login")
                .name("user")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();

        userRepo.createUser(user);
        filmRepo.setLikeOnFilm(film2.getId(), user.getId());

        List<FilmDao> list = new ArrayList<>(filmRepo.findNPopular(1L, 1L, 2025));
        assertAll(() -> {
            assertFalse(list.isEmpty());
            assertEquals(film2, list.getFirst());
        });
    }

    @Test
    void deleteLikeFromFilmTest() {
        MpaDao mpaDao = new MpaDao(1L, AgeRating.fromValue("G"));
        GenreDao genreDao = new GenreDao(1L, Genre.COMEDY);
        ArrayList<GenreDao> genre = new ArrayList<>();
        genre.add(genreDao);
        FilmDao film = FilmDao.builder()
                .name("film1")
                .description("super-film1")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(genre)
                .mpa(mpaDao)
                .build();
        FilmDao film2 = FilmDao.builder()
                .name("film2")
                .description("super-film2")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(genre)
                .mpa(mpaDao)
                .build();

        filmRepo.createFilm(film);
        filmRepo.createFilm(film2);

        UserDao user = UserDao.builder()
                .email("email@email.ru")
                .login("login")
                .name("user")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();

        userRepo.createUser(user);
        filmRepo.setLikeOnFilm(film2.getId(), user.getId());
        filmRepo.setLikeOnFilm(film.getId(), user.getId());
        filmRepo.deleteLikeFromFilm(film2.getId(), user.getId());

        List<FilmDao> list = new ArrayList<>(filmRepo.findNPopular(1L, 1L, 2025));
        assertAll(() -> {
            assertFalse(list.isEmpty());
            assertEquals(film, list.getFirst());
        });
    }

    @Test
    void findNPopularTest() {
        MpaDao mpaDao = new MpaDao(1L, AgeRating.fromValue("G"));
        GenreDao genreDao = new GenreDao(1L, Genre.COMEDY);
        ArrayList<GenreDao> genre = new ArrayList<>();
        genre.add(genreDao);
        FilmDao film = FilmDao.builder()
                .name("film1")
                .description("super-film1")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(genre)
                .mpa(mpaDao)
                .build();
        FilmDao film2 = FilmDao.builder()
                .name("film2")
                .description("super-film2")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(genre)
                .mpa(mpaDao)
                .build();
        FilmDao film3 = FilmDao.builder()
                .name("film3")
                .description("super-film3")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(genre)
                .mpa(mpaDao)
                .build();

        filmRepo.createFilm(film);
        filmRepo.createFilm(film2);
        filmRepo.createFilm(film3);

        UserDao user = UserDao.builder()
                .email("email@email.ru")
                .login("login")
                .name("user")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        UserDao user2 = UserDao.builder()
                .email("imail@email.ru")
                .login("user2")
                .name("friend")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        UserDao user3 = UserDao.builder()
                .email("ya@email.ru")
                .login("user3")
                .name("anotherFriend")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();

        userRepo.createUser(user);
        userRepo.createUser(user2);
        userRepo.createUser(user3);

        filmRepo.setLikeOnFilm(film.getId(), user.getId());

        filmRepo.setLikeOnFilm(film2.getId(), user.getId());
        filmRepo.setLikeOnFilm(film2.getId(), user2.getId());

        filmRepo.setLikeOnFilm(film3.getId(), user.getId());
        filmRepo.setLikeOnFilm(film3.getId(), user2.getId());
        filmRepo.setLikeOnFilm(film3.getId(), user3.getId());

        List<FilmDao> films = new ArrayList<>(filmRepo.findNPopular(2L, 1L, 2025));

        assertAll(() -> {
            assertFalse(films.isEmpty());
            assertEquals(2, films.size());
            assertEquals(film3, films.getFirst());
            assertEquals(film2, films.getLast());
        });
    }

    @Test
    void getRecommendationTest() {
        MpaDao mpaDao = new MpaDao(1L, AgeRating.fromValue("G"));
        FilmDao film = FilmDao.builder()
                .name("film1")
                .description("super-film1")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(new ArrayList<>())
                .mpa(mpaDao)
                .build();
        FilmDao film2 = FilmDao.builder()
                .name("film2")
                .description("super-film2")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(new ArrayList<>())
                .mpa(mpaDao)
                .build();
        FilmDao film3 = FilmDao.builder()
                .name("film3")
                .description("super-film3")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(new ArrayList<>())
                .mpa(mpaDao)
                .build();

        filmRepo.createFilm(film);
        filmRepo.createFilm(film2);
        filmRepo.createFilm(film3);

        UserDao user = UserDao.builder()
                .email("email@email.ru")
                .login("login")
                .name("user")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        UserDao user2 = UserDao.builder()
                .email("imail@email.ru")
                .login("user2")
                .name("friend")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        UserDao user3 = UserDao.builder()
                .email("ya@email.ru")
                .login("user3")
                .name("anotherFriend")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();

        userRepo.createUser(user);
        userRepo.createUser(user2);
        userRepo.createUser(user3);

        filmRepo.setLikeOnFilm(film.getId(), user.getId());

        filmRepo.setLikeOnFilm(film2.getId(), user.getId());
        filmRepo.setLikeOnFilm(film2.getId(), user2.getId());

        filmRepo.setLikeOnFilm(film3.getId(), user.getId());
        filmRepo.setLikeOnFilm(film3.getId(), user2.getId());
        filmRepo.setLikeOnFilm(film3.getId(), user3.getId());

        List<FilmDao> recommendations = new ArrayList<>(filmRepo.getRecommendations(user2.getId()));
        System.out.println(recommendations);
        assertEquals(recommendations.get(0).getName(), "film1");
    }

    @Test
    void getRecommendationWhenAllLikesAreTheSameTest() {
        MpaDao mpaDao = new MpaDao(1L, AgeRating.fromValue("G"));
        FilmDao film = FilmDao.builder()
                .name("film1")
                .description("super-film1")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(new ArrayList<>())
                .mpa(mpaDao)
                .build();
        FilmDao film2 = FilmDao.builder()
                .name("film2")
                .description("super-film2")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(new ArrayList<>())
                .mpa(mpaDao)
                .build();
        FilmDao film3 = FilmDao.builder()
                .name("film3")
                .description("super-film3")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(new ArrayList<>())
                .mpa(mpaDao)
                .build();

        filmRepo.createFilm(film);
        filmRepo.createFilm(film2);
        filmRepo.createFilm(film3);

        UserDao user = UserDao.builder()
                .email("email@email.ru")
                .login("login")
                .name("user")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        UserDao user2 = UserDao.builder()
                .email("imail@email.ru")
                .login("user2")
                .name("friend")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        UserDao user3 = UserDao.builder()
                .email("ya@email.ru")
                .login("user3")
                .name("anotherFriend")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();

        userRepo.createUser(user);
        userRepo.createUser(user2);
        userRepo.createUser(user3);

        filmRepo.setLikeOnFilm(film.getId(), user.getId());
        filmRepo.setLikeOnFilm(film.getId(), user2.getId());
        filmRepo.setLikeOnFilm(film.getId(), user3.getId());

        filmRepo.setLikeOnFilm(film2.getId(), user.getId());
        filmRepo.setLikeOnFilm(film2.getId(), user2.getId());
        filmRepo.setLikeOnFilm(film2.getId(), user3.getId());

        filmRepo.setLikeOnFilm(film3.getId(), user.getId());
        filmRepo.setLikeOnFilm(film3.getId(), user2.getId());
        filmRepo.setLikeOnFilm(film3.getId(), user3.getId());

        List<FilmDao> recommendations = new ArrayList<>(filmRepo.getRecommendations(user2.getId()));
        System.out.println(recommendations);
        assertTrue(recommendations.isEmpty());
    }

    @Test
    void getRecommendationIfAllLikesAreDifferent() {
        MpaDao mpaDao = new MpaDao(1L, AgeRating.fromValue("G"));
        FilmDao film = FilmDao.builder()
                .name("film1")
                .description("super-film1")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(new ArrayList<>())
                .mpa(mpaDao)
                .build();
        FilmDao film2 = FilmDao.builder()
                .name("film2")
                .description("super-film2")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(new ArrayList<>())
                .mpa(mpaDao)
                .build();
        FilmDao film3 = FilmDao.builder()
                .name("film3")
                .description("super-film3")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(new ArrayList<>())
                .mpa(mpaDao)
                .build();

        filmRepo.createFilm(film);
        filmRepo.createFilm(film2);
        filmRepo.createFilm(film3);

        UserDao user = UserDao.builder()
                .email("email@email.ru")
                .login("login")
                .name("user")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        UserDao user2 = UserDao.builder()
                .email("imail@email.ru")
                .login("user2")
                .name("friend")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        UserDao user3 = UserDao.builder()
                .email("ya@email.ru")
                .login("user3")
                .name("anotherFriend")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();

        userRepo.createUser(user);
        userRepo.createUser(user2);
        userRepo.createUser(user3);

        filmRepo.setLikeOnFilm(film.getId(), user.getId());

        filmRepo.setLikeOnFilm(film2.getId(), user2.getId());

        filmRepo.setLikeOnFilm(film3.getId(), user3.getId());

        List<FilmDao> recommendations = new ArrayList<>(filmRepo.getRecommendations(user2.getId()));
        System.out.println(recommendations);
        assertTrue(recommendations.isEmpty());
    }

    @Test
    void deleteFilmTest() {
        MpaDao mpaDao = new MpaDao(1L, AgeRating.fromValue("G"));
        GenreDao genreDao = new GenreDao(1L, Genre.COMEDY);
        ArrayList<GenreDao> genre = new ArrayList<>();
        genre.add(genreDao);
        FilmDao film = FilmDao.builder()
                .name("film1")
                .description("super-film1")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(genre)
                .mpa(mpaDao)
                .build();
        FilmDao film2 = FilmDao.builder()
                .name("film2")
                .description("super-film2")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(genre)
                .mpa(mpaDao)
                .build();
        FilmDao film3 = FilmDao.builder()
                .name("film3")
                .description("super-film3")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(genre)
                .mpa(mpaDao)
                .build();

        Long id = filmRepo.createFilm(film).getId();
        filmRepo.createFilm(film2);
        filmRepo.createFilm(film3);

        assertEquals(3, filmRepo.findAll().size());
        filmRepo.deleteFilm(id);
        assertEquals(2, filmRepo.findAll().size());
    }

    @Test
    void filmShouldBeRemovedFromLikedFilmsAfterDelete() {
        MpaDao mpaDao = new MpaDao(1L, AgeRating.fromValue("G"));
        GenreDao genreDao = new GenreDao(1L, Genre.COMEDY);
        ArrayList<GenreDao> genre = new ArrayList<>();
        genre.add(genreDao);
        FilmDao film = FilmDao.builder()
                .name("film1")
                .description("super-film1")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(genre)
                .mpa(mpaDao)
                .build();

        Long id = filmRepo.createFilm(film).getId();

        UserDao user = UserDao.builder()
                .email("email@email.ru")
                .login("login")
                .name("user")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();

        userRepo.createUser(user);

        List<FilmDao> list = new ArrayList<>(filmRepo.findNPopular(1L, 1L, 2025));
        assertEquals(1, list.size());

        filmRepo.deleteFilm(id);
        assertEquals(0, filmRepo.findNPopular(1L, 1L, 2025).size());
    }

    @Test
    void filmShouldBeRemovedFromFilmGenresAfterDelete() {
        MpaDao mpaDao = new MpaDao(1L, AgeRating.fromValue("G"));
        GenreDao genreDao = new GenreDao(1L, Genre.COMEDY);
        ArrayList<GenreDao> genre = new ArrayList<>();
        genre.add(genreDao);
        FilmDao film = FilmDao.builder()
                .name("film1")
                .description("super-film1")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(genre)
                .mpa(mpaDao)
                .build();

        Long id = filmRepo.createFilm(film).getId();

        String SQL = "SELECT COUNT(genre_id) FROM film_genres WHERE film_id = ?";

        Long genreId = jdbc.queryForObject(SQL, Long.class, id);
        assertEquals(1, genreId);

        filmRepo.deleteFilm(id);
        Long genreId1 = jdbc.queryForObject(SQL, Long.class, id);
        assertEquals(0, genreId1);
    }

}