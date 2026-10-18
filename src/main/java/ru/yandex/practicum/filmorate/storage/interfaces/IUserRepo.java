package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.dao.UserDao;

import java.util.Collection;
import java.util.Optional;

public interface IUserRepo {

    Collection<UserDao> findAll();

    UserDao createUser(UserDao userDao);

    UserDao updateUser(UserDao userDao);

    Optional<UserDao> findUserById(Long id);

    Collection<UserDao> findFriends(Long userId);

    void addFriend(Long userId, Long friendId);

    void removeFromFriends(Long userId, Long friendId);

    void deleteUser(Long userId);
}
