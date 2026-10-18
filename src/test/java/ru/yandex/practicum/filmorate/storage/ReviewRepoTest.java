package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
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
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dao.enums.AgeRating;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.service.enums.LikeOnReviewActions;
import ru.yandex.practicum.filmorate.storage.mapper.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({ReviewMapper.class, FilmMapper.class, UserMapper.class})
public class ReviewRepoTest {

    private ReviewRepo reviewRepo;
    private FilmRepo filmRepo;
    private UserRepo userRepo;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private RowMapper<ReviewDao> mapper;

    @Autowired
    private RowMapper<UserDao> userDaoRowMapper;

    @BeforeEach
    void init() {
        reviewRepo = new ReviewRepo(
                jdbc,
                mapper
        );
        filmRepo = new FilmRepo(
                jdbc,
                new FilmExtractor(),
                new SingleFilmExtractor()
        );
        userRepo = new UserRepo(
                jdbc,
                userDaoRowMapper
        );
    }

    private static FilmDao createFilm() {
        MpaDao mpaDao = new MpaDao(1L, AgeRating.fromValue("G"));

        return FilmDao.builder()
                .name("film")
                .description("super-film")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(new ArrayList<>())
                .mpa(mpaDao)
                .build();
    }

    private static UserDao createUser() {
        return UserDao.builder()
                .email("email@email.ru")
                .login("login")
                .name("user")
                .birthday(LocalDate.of(2000,  2, 20))
                .build();
    }

    @Test
    void postReviewTest() {
        UserDao user = userRepo.createUser(createUser());
        FilmDao film = filmRepo.createFilm(createFilm());
        ReviewDao reviewDao = ReviewDao.builder()
                .content("Test content")
                .isPositive(true)
                .userId(user.getId())
                .filmId(film.getId())
                .useful(0)
                .build();

        assertDoesNotThrow(() -> reviewRepo.postReview(reviewDao));
    }

    @Test
    void getReviewByIdTest() {
        UserDao user = userRepo.createUser(createUser());
        FilmDao film = filmRepo.createFilm(createFilm());

        ReviewDao reviewDao = ReviewDao.builder()
                .content("Test content")
                .isPositive(true)
                .userId(user.getId())
                .filmId(film.getId())
                .useful(0)
                .build();

        assertDoesNotThrow(() -> reviewRepo.postReview(reviewDao));
        Assertions.assertTrue(reviewRepo.getReviewById(reviewDao.getId()).isPresent());
        Assertions.assertEquals(reviewDao, reviewRepo.getReviewById(reviewDao.getId()).get());
        assertAll(() -> {
            assertEquals("Test content", reviewDao.getContent());
            assertEquals(true, reviewDao.getIsPositive());
            assertEquals(user.getId(), reviewDao.getUserId());
            assertEquals(film.getId(), reviewDao.getFilmId());
            assertEquals(0, reviewDao.getUseful());
        });
    }

    @Test
    void putReviewTest() {
        UserDao user = userRepo.createUser(createUser());
        FilmDao film = filmRepo.createFilm(createFilm());

        ReviewDao reviewDao = ReviewDao.builder()
                .content("Test content")
                .isPositive(true)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        ReviewDao reviewDao1 = ReviewDao.builder()
                .content("Test content1")
                .isPositive(false)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        Long id = reviewRepo.postReview(reviewDao).getId();
        reviewDao1.setId(id);

        assertDoesNotThrow(() -> reviewRepo.putReview(reviewDao1));
        Optional<ReviewDao> reviews = reviewRepo.getReviewById(id);

        assertTrue(reviews.isPresent());
        ReviewDao reviewFirst = reviews.get();

        Assertions.assertAll(() -> {
            assertEquals(reviewFirst.getId(), reviewDao1.getId());
            assertEquals(reviewFirst.getContent(), reviewDao1.getContent());
            assertEquals(reviewFirst.getIsPositive(), reviewDao1.getIsPositive());
            assertEquals(reviewFirst.getUserId(), reviewDao1.getUserId());
            assertEquals(reviewFirst.getFilmId(), reviewDao1.getFilmId());
        });
    }

    @Test
    void getReviewByFilmIdTest() {
        UserDao user = userRepo.createUser(createUser());
        FilmDao film = filmRepo.createFilm(createFilm());
        UserDao user2 = UserDao.builder()
                .email("email1@email.ru")
                .login("login1")
                .name("user1")
                .birthday(LocalDate.of(2001,  2, 21))
                .build();
        userRepo.createUser(user2);

        ReviewDao review1 = ReviewDao.builder()
                .content("Test content")
                .isPositive(true)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        ReviewDao review2 = ReviewDao.builder()
                .content("Test content1")
                .isPositive(false)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        reviewRepo.postReview(review1);
        reviewRepo.postReview(review2);

        List<ReviewDao> reviews = new  ArrayList<>(reviewRepo.getReviewByFilmId(film.getId(), 10));

        assertEquals(2, reviews.size());
    }

    @Test
    void deleteReviewTest() {
        UserDao user = userRepo.createUser(createUser());
        FilmDao film = filmRepo.createFilm(createFilm());

        ReviewDao review = ReviewDao.builder()
                .content("Test content")
                .isPositive(true)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        reviewRepo.postReview(review);
        ReviewDao review2 = ReviewDao.builder()
                .content("Test content1")
                .isPositive(false)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        Long id = reviewRepo.postReview(review2).getId();

        List<ReviewDao> reviews = new ArrayList<>(reviewRepo.getReviewByFilmId(film.getId(), 10));
        assertEquals(2, reviews.size());

        assertDoesNotThrow(() -> reviewRepo.deleteReview(id));
        List<ReviewDao> reviews1 = new ArrayList<>(reviewRepo.getReviewByFilmId(film.getId(), 10));
        assertEquals(1, reviews1.size());
    }

    @Test
    void addLikeTest() {
        UserDao user = userRepo.createUser(createUser());
        FilmDao film = filmRepo.createFilm(createFilm());
        UserDao user1 = UserDao.builder()
                .email("email1@email.ru")
                .login("login1")
                .name("user1")
                .birthday(LocalDate.of(2001,  2, 21))
                .build();
        Long userId = userRepo.createUser(user1).getId();

        ReviewDao review = ReviewDao.builder()
                .content("Test content")
                .isPositive(true)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        Long reviewId = reviewRepo.postReview(review).getId();

        assertDoesNotThrow(() -> reviewRepo.reviewActions(reviewId, userId, LikeOnReviewActions.ADD_LIKE));

        Optional<ReviewDao> reviewById = reviewRepo.getReviewById(reviewId);
        assertTrue(reviewById.isPresent());
        ReviewDao reviewDao = reviewById.get();

        assertEquals(1, reviewRepo.getReviewByFilmId(film.getId(), 10).size());
        assertEquals(1, reviewDao.getUseful());
    }

    @Test
    void deleteLikeTest() {
        UserDao user = userRepo.createUser(createUser());
        FilmDao film = filmRepo.createFilm(createFilm());
        UserDao user1 = UserDao.builder()
                .email("email1@email.ru")
                .login("login1")
                .name("user1")
                .birthday(LocalDate.of(2001,  2, 21))
                .build();
        Long userId = userRepo.createUser(user1).getId();

        ReviewDao review = ReviewDao.builder()
                .content("Test content")
                .isPositive(true)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        Long reviewId = reviewRepo.postReview(review).getId();

        assertDoesNotThrow(() -> reviewRepo.reviewActions(reviewId, userId, LikeOnReviewActions.ADD_LIKE));

        Optional<ReviewDao> reviewById = reviewRepo.getReviewById(reviewId);
        assertTrue(reviewById.isPresent());
        ReviewDao reviewDao = reviewById.get();

        assertEquals(1, reviewRepo.getReviewByFilmId(film.getId(), 10).size());
        assertEquals(1, reviewDao.getUseful());

        assertDoesNotThrow(() -> reviewRepo.reviewActions(reviewId, userId, LikeOnReviewActions.DELETE_LIKE));

        Optional<ReviewDao> reviewById1 = reviewRepo.getReviewById(reviewId);
        assertTrue(reviewById1.isPresent());
        ReviewDao reviewDao1 = reviewById1.get();

        assertEquals(0, reviewDao1.getUseful());
    }

    @Test
    void addDislikeTest() {
        UserDao user = userRepo.createUser(createUser());
        FilmDao film = filmRepo.createFilm(createFilm());
        UserDao user1 = UserDao.builder()
                .email("email1@email.ru")
                .login("login1")
                .name("user1")
                .birthday(LocalDate.of(2001,  2, 21))
                .build();
        Long userId = userRepo.createUser(user1).getId();

        ReviewDao review = ReviewDao.builder()
                .content("Test content")
                .isPositive(true)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        Long reviewId = reviewRepo.postReview(review).getId();

        assertDoesNotThrow(() -> reviewRepo.reviewActions(reviewId, userId, LikeOnReviewActions.ADD_DISLIKE));


        Optional<ReviewDao> reviewById = reviewRepo.getReviewById(reviewId);
        assertTrue(reviewById.isPresent());
        ReviewDao reviewDao = reviewById.get();

        assertEquals(1, reviewRepo.getReviewByFilmId(film.getId(), 10).size());
        assertEquals(-1, reviewDao.getUseful());
    }

    @Test
    void deleteDislikeTest() {
        UserDao user = userRepo.createUser(createUser());
        FilmDao film = filmRepo.createFilm(createFilm());
        UserDao user1 = UserDao.builder()
                .email("email1@email.ru")
                .login("login1")
                .name("user1")
                .birthday(LocalDate.of(2001,  2, 21))
                .build();
        Long userId = userRepo.createUser(user1).getId();

        ReviewDao review = ReviewDao.builder()
                .content("Test content")
                .isPositive(true)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        Long reviewId = reviewRepo.postReview(review).getId();

        assertDoesNotThrow(() -> reviewRepo.reviewActions(reviewId, userId, LikeOnReviewActions.ADD_DISLIKE));

        Optional<ReviewDao> reviewById = reviewRepo.getReviewById(reviewId);
        assertTrue(reviewById.isPresent());
        ReviewDao reviewDao = reviewById.get();

        assertEquals(1, reviewRepo.getReviewByFilmId(film.getId(), 10).size());
        assertEquals(-1, reviewDao.getUseful());

        assertDoesNotThrow(() -> reviewRepo.reviewActions(reviewId, userId, LikeOnReviewActions.DELETE_DISLIKE));
        Optional<ReviewDao> reviewById1 = reviewRepo.getReviewById(reviewId);
        assertTrue(reviewById1.isPresent());
        ReviewDao reviewDao1 = reviewById1.get();
        assertEquals(0, reviewDao1.getUseful());
    }
}
