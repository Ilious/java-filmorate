package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dto.UserRecord;

import java.util.Collection;

public interface IUserService {

    UserDao putUser(UserRecord userRecord);

    UserDao postUser(UserRecord userRecord);

    Collection<UserDao> getAll();

    void addFriend(Long id, Long friendId);

    UserDao getUserById(Long id);

    Collection<UserDao> getFriends(Long id);

    void removeUserFromFriends(Long id, Long friendId);

    Collection<UserDao> getFriendsInCommon(Long id, Long friendId);
}
