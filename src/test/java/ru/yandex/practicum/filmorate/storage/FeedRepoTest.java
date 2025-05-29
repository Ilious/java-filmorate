package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.service.enums.EntityType;
import ru.yandex.practicum.filmorate.service.enums.Operation;
import ru.yandex.practicum.filmorate.storage.mapper.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FeedMapper.class, ReviewMapper.class, FilmMapper.class, UserMapper.class})
class FeedRepoTest {

    private FeedRepo feedRepo;

    private UserRepo userRepo;

    private FilmRepo filmRepo;

    private ReviewRepo reviewRepo;

    @Autowired
    private RowMapper<FeedDao> feedDaoRowMapper;

    @Autowired
    private RowMapper<FilmDao> filmDaoRowMapper;

    @Autowired
    private RowMapper<UserDao> userDaoRowMapper;

    @Autowired
    private RowMapper<ReviewDao> reviewDaoRowMapper;

    @Autowired
    private JdbcTemplate jdbc;

    Long userId;

    @BeforeEach
    void init() {
        feedRepo = new FeedRepo(
                jdbc,
                feedDaoRowMapper
        );
        userRepo = new UserRepo(
                jdbc,
                userDaoRowMapper
        );
        filmRepo = new FilmRepo(
                jdbc,
                filmDaoRowMapper,
                new FilmExtractor(),
                new SingleFilmExtractor()
        );
        reviewRepo = new ReviewRepo(
                jdbc,
                reviewDaoRowMapper
        );

        userId = userRepo.createUser(UserDao.builder()
                .name("John")
                .login("JohnWick")
                .email("email@email.com")
                .birthday(LocalDate.now().minusDays(1))
                .build()).getId();
    }

    @AfterEach
    void cleanupDatabase() {
        jdbc.update("DELETE FROM feeds");
        jdbc.update("DELETE FROM films");
        jdbc.update("DELETE FROM users");
    }

    @Test
    void findAll() {
        long idx = 1L;
        long userId = 1L;

        FeedDao firstFeed = FeedDao.builder()
                .eventType(EntityType.LIKE)
                .operation(Operation.ADD)
                .timestamp(Instant.now())
                .userId(userId)
                .entityId(idx)
                .build();
        feedRepo.createFeed(firstFeed);
        firstFeed.setId(idx++);
        feedRepo.createFeed(FeedDao.builder()
                .eventType(EntityType.LIKE)
                .operation(Operation.REMOVE)
                .timestamp(Instant.now())
                .userId(userId)
                .entityId(10L)
                .build());
        idx++;
        feedRepo.createFeed(FeedDao.builder()
                .eventType(EntityType.REVIEW)
                .operation(Operation.ADD)
                .timestamp(Instant.now())
                .userId(userId)
                .entityId(8L)
                .build());
        idx++;
        feedRepo.createFeed(FeedDao.builder()
                .eventType(EntityType.REVIEW)
                .operation(Operation.UPDATE)
                .timestamp(Instant.now())
                .userId(userId)
                .entityId(11L)
                .build());
        idx++;
        feedRepo.createFeed(FeedDao.builder()
                .eventType(EntityType.REVIEW)
                .operation(Operation.REMOVE)
                .timestamp(Instant.now())
                .userId(userId)
                .entityId(20L)
                .build());
        idx++;
        feedRepo.createFeed(FeedDao.builder()
                .eventType(EntityType.FRIEND)
                .operation(Operation.ADD)
                .timestamp(Instant.now())
                .userId(userId)
                .entityId(21L)
                .build());
        idx++;
        FeedDao lastFeed = FeedDao.builder()
                .eventType(EntityType.FRIEND)
                .operation(Operation.REMOVE)
                .timestamp(Instant.now())
                .userId(userId)
                .entityId(21L)
                .build();
        lastFeed.setId(idx);
        feedRepo.createFeed(lastFeed);

        List<FeedDao> all = new ArrayList<>(feedRepo.findAll());

        long lastIdx = idx;
        assertFalse(all.isEmpty());
        assertEquals(lastIdx, all.size());

        assertAll(() -> {
            assertEquals(firstFeed.getId(), all.getFirst().getId());
            assertEquals(firstFeed.getEventType(), all.getFirst().getEventType());
            assertEquals(firstFeed.getOperation(), all.getFirst().getOperation());
            assertEquals(
                    firstFeed.getTimestamp().truncatedTo(ChronoUnit.SECONDS),
                    all.getFirst().getTimestamp().truncatedTo(ChronoUnit.SECONDS))
            ;
            assertEquals(firstFeed.getUserId(), all.getFirst().getUserId());
            assertEquals(firstFeed.getEntityId(), all.getFirst().getEntityId());
        });
        assertAll(() -> {
            assertEquals(lastFeed.getId(), all.getLast().getId());
            assertEquals(lastFeed.getEventType(), all.getLast().getEventType());
            assertEquals(lastFeed.getOperation(), all.getLast().getOperation());
            assertEquals(
                    lastFeed.getTimestamp().truncatedTo(ChronoUnit.SECONDS),
                    all.getLast().getTimestamp().truncatedTo(ChronoUnit.SECONDS))
            ;
            assertEquals(lastFeed.getUserId(), all.getLast().getUserId());
            assertEquals(lastFeed.getEntityId(), all.getLast().getEntityId());
        });
    }

    @Test
    void createFeed() {
        FeedDao feed = FeedDao.builder()
                .eventType(EntityType.FRIEND)
                .operation(Operation.ADD)
                .timestamp(Instant.now())
                .userId(userId)
                .entityId(1L)
                .build();


        assertDoesNotThrow(() -> feedRepo.createFeed(feed));
    }

    @Test
    void getByUserId() {
        Long anotherUserId = userRepo.createUser(UserDao.builder()
                .name("John")
                .login("JohnWickClone")
                .email("clone@email.com")
                .birthday(LocalDate.now().minusDays(1))
                .build()).getId();

        FeedDao feed = FeedDao.builder()
                .eventType(EntityType.FRIEND)
                .operation(Operation.ADD)
                .timestamp(Instant.now())
                .userId(userId)
                .entityId(1L)
                .build();
        feedRepo.createFeed(feed);
        FeedDao feed2 = FeedDao.builder()
                .eventType(EntityType.REVIEW)
                .operation(Operation.REMOVE)
                .timestamp(Instant.now())
                .userId(anotherUserId)
                .entityId(2L)
                .build();
        feedRepo.createFeed(feed2);

        List<FeedDao> byUserId = new ArrayList<>(feedRepo.getByUserId(anotherUserId));

        assertFalse(byUserId.isEmpty());
        assertEquals(1, byUserId.size());
        FeedDao feedSecondUser = byUserId.getFirst();
        assertAll(() -> {
            assertEquals(feed2.getEventType(), feedSecondUser.getEventType());
            assertEquals(feed2.getOperation(), feedSecondUser.getOperation());
            assertEquals(
                    feed2.getTimestamp().truncatedTo(ChronoUnit.SECONDS),
                    feedSecondUser.getTimestamp().truncatedTo(ChronoUnit.SECONDS))
            ;
            assertEquals(feed2.getUserId(), feedSecondUser.getUserId());
            assertEquals(feed2.getEntityId(), feedSecondUser.getEntityId());
        });
    }
}