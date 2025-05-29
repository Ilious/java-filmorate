package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.storage.interfaces.IUserRepo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepo extends BaseRepo<UserDao> implements IUserRepo {

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users where id = ?";

    private static final String INSERT_QUERY = "INSERT INTO users (name, login, email, birthday) " +
            "VALUES (?, ?, ?, ?)";

    private static final String FIND_FRIENDS_BY_USER_ID_QUERY = "SELECT * FROM users " +
            "WHERE id IN (" +
            "SELECT usr_friend.friend_id FROM user_friends usr_friend " +
            "WHERE usr_friend.user_id = ?" +
            ")";

    private static final String UPDATE_QUERY = "UPDATE users " +
            "SET name = ?, login = ?, email = ?, birthday = ? WHERE id = ?";

    private static final String INSERT_FRIEND_QUERY = "INSERT INTO user_friends(user_id, friend_id) " +
            "VALUES (?, ?)";

    private static final String DELETE_FRIEND_QUERY = "DELETE FROM user_friends " +
            "WHERE user_id = ? AND friend_id = ?";


    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id = ?";

    public UserRepo(JdbcTemplate jdbc, RowMapper<UserDao> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<UserDao> findAll() {
        log.trace("UserRepo.findAll: findAll");

        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public UserDao createUser(UserDao userDao) {
        log.trace("UserRepo.createUser: user {}", userDao);

        long id = insert(
                INSERT_QUERY,
                userDao.getName(),
                userDao.getLogin(),
                userDao.getEmail(),
                userDao.getBirthday()
        );
        userDao.setId(id);

        return userDao;
    }

    @Override
    public UserDao updateUser(UserDao userDao) {
        log.trace("UserRepo.updateUser: user {}", userDao);

        update(
                UPDATE_QUERY,
                userDao.getName(),
                userDao.getLogin(),
                userDao.getEmail(),
                userDao.getBirthday(),
                userDao.getId()
        );
        return userDao;
    }

    @Override
    public Optional<UserDao> findUserById(Long id) {
        log.trace("UserRepo.findUserById: by id {}", id);

        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public Collection<UserDao> findFriends(Long userId) {
        log.trace("UserRepo.findFriends: by userId {}", userId);

        return findMany(FIND_FRIENDS_BY_USER_ID_QUERY, userId);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        log.trace("UserRepo.addFriend: userId {}, friendId {}", userId, friendId);

        insertNoKey(
                INSERT_FRIEND_QUERY, userId, friendId
        );
    }

    @Override
    public void removeFromFriends(Long userId, Long friendId) {
        log.trace("UserRepo.removeFromFriends: by userId {}", userId);

        delete(
                DELETE_FRIEND_QUERY, userId, friendId
        );
    }

    @Override
    public void deleteUser(Long userId) {
        log.trace("UserRepo.deleteUser: by userId {}", userId);

        delete(
                DELETE_USER_QUERY, userId
        );
    }
}
