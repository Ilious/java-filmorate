package ru.yandex.practicum.filmorate.storage;

import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@JdbcTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@Import(UserMapper.class)
@NoArgsConstructor(onConstructor_ = @Autowired)
class UserRepoTest {

    @Autowired
    private RowMapper<UserDao> mapper;

    @Autowired
    private JdbcTemplate jdbc;

    private UserRepo userRepo;

    @BeforeEach
    void init() {
        userRepo = new UserRepo(
                jdbc,
                mapper
        );
    }


    @Test
    void createUserTest() {
        UserDao dao = UserDao.builder()
                .email("email@email.ru")
                .login("login")
                .name("user")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();

        assertDoesNotThrow(() -> userRepo.createUser(dao));
    }

    @Test
    void findAllTest() {
        UserDao dao = UserDao.builder()
                .email("email@email.ru")
                .login("login")
                .name("user")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        UserDao dao2 = UserDao.builder()
                .email("imail@email.ru")
                .login("login2")
                .name("user2")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        UserDao dao3 = UserDao.builder()
                .email("eimail@email.ru")
                .login("login3")
                .name("user3")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();

        userRepo.createUser(dao);
        userRepo.createUser(dao2);
        userRepo.createUser(dao3);

        assertAll(() -> {
            assertFalse(userRepo.findAll().isEmpty());
            assertEquals(3, userRepo.findAll().size());
        });
    }

    @Test
    void updateUserTest() {
        UserDao dao = UserDao.builder()
                .email("email@email.ru")
                .login("login")
                .name("user")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        UserDao upd = UserDao.builder()
                .email("updated@email.ru")
                .login("login")
                .name("updated")
                .birthday(LocalDate.of(2001, 1, 10))
                .build();

        Long id = userRepo.createUser(dao).getId();
        upd.setId(id);
        assertDoesNotThrow(() -> userRepo.updateUser(upd));
        Optional<UserDao> userById = userRepo.findUserById(id);

        assertTrue(userById.isPresent());
        UserDao user = userById.get();
        assertAll(() -> {
            assertEquals(upd.getId(), user.getId());
            assertEquals(upd.getName(), user.getName());
            assertEquals(upd.getLogin(), user.getLogin());
            assertEquals(upd.getEmail(), user.getEmail());
            assertEquals(upd.getBirthday(), user.getBirthday());
        });
    }

    @Test
    void findUserByIdTest() {
        UserDao dao = UserDao.builder()
                .email("email@email.ru")
                .login("login")
                .name("user")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();

        Long id = userRepo.createUser(dao).getId();
        Optional<UserDao> userById = userRepo.findUserById(id);

        assertAll(() -> {
            assertTrue(userById.isPresent());
            assertEquals(dao, userById.get());
        });
    }


    @Test
    void addFriendTest() {
        UserDao user = UserDao.builder()
                .email("email@email.ru")
                .login("login")
                .name("user")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        UserDao friend = UserDao.builder()
                .email("imail@email.ru")
                .login("login2")
                .name("friend")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();

        userRepo.createUser(user);
        userRepo.createUser(friend);
        userRepo.addFriend(user.getId(), friend.getId());

        List<UserDao> userFriends = new ArrayList<>(userRepo.findFriends(user.getId()));

        assertAll(() -> {
            assertFalse(userFriends.isEmpty());
            assertEquals(1, userFriends.size());
            assertEquals(userFriends.getFirst(), friend);
        });
    }

    @Test
    void removeFriendTest() {
        UserDao user = UserDao.builder()
                .email("email@email.ru")
                .login("login")
                .name("user")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        UserDao friend = UserDao.builder()
                .email("imail@email.ru")
                .login("login2")
                .name("friend")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();

        userRepo.createUser(user);
        userRepo.createUser(friend);
        userRepo.addFriend(user.getId(), friend.getId());
        userRepo.removeFromFriends(user.getId(), friend.getId());

        List<UserDao> userFriends = new ArrayList<>(userRepo.findFriends(user.getId()));

        assertTrue(userFriends.isEmpty());
    }

    @Test
    void findFriendsTest() {
        UserDao user = UserDao.builder()
                .email("email@email.ru")
                .login("login")
                .name("user")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        UserDao friend = UserDao.builder()
                .email("imail@email.ru")
                .login("login2")
                .name("friend")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        UserDao friend2 = UserDao.builder()
                .email("ya@email.ru")
                .login("login3")
                .name("anotherFriend")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();

        userRepo.createUser(user);
        userRepo.createUser(friend);
        userRepo.createUser(friend2);
        userRepo.addFriend(user.getId(), friend.getId());
        userRepo.addFriend(user.getId(), friend2.getId());

        List<UserDao> userFriends = new ArrayList<>(userRepo.findFriends(user.getId()));

        assertAll(() -> {
            assertFalse(userFriends.isEmpty());
            assertEquals(2, userFriends.size());
        });
    }

    @Test
    void deleteUserTest() {
        UserDao user = UserDao.builder()
                .email("email@email.ru")
                .login("login")
                .name("user")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        UserDao user1 = UserDao.builder()
                .email("imail@email.ru")
                .login("login2")
                .name("friend")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        UserDao user2 = UserDao.builder()
                .email("ya@email.ru")
                .login("login3")
                .name("anotherFriend")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();

        userRepo.createUser(user);
        userRepo.createUser(user1);
        userRepo.createUser(user2);

        assertEquals(3, userRepo.findAll().size());
        userRepo.deleteUser(user.getId());
        assertEquals(2, userRepo.findAll().size());
    }

    @Test
    void userShouldBeRemovedFromUserFriendsAfterDelete() {
        UserDao user = UserDao.builder()
                .email("email@email.ru")
                .login("login")
                .name("user")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
        UserDao friend = UserDao.builder()
                .email("imail@email.ru")
                .login("login2")
                .name("friend")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();

        userRepo.createUser(user);
        userRepo.createUser(friend);

        assertEquals(2, userRepo.findAll().size());

        userRepo.addFriend(user.getId(), friend.getId());
        List<UserDao> friends = new ArrayList<>(userRepo.findFriends(user.getId()));

        assertEquals(1, friends.size());

        userRepo.deleteUser(friend.getId());
        List<UserDao> friends1 = new ArrayList<>(userRepo.findFriends(user.getId()));

        assertEquals(0, friends1.size());
    }
}