package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.pojo.User;
import ru.yandex.practicum.filmorate.dto.UserRecord;

import java.util.Collection;

public interface IUserService {

    User putUser(UserRecord userRecord);

    User postUser(UserRecord userRecord);

    Collection<User> getAll();

    User addFriend(Long id, Long friendId);

    Collection<User> getFriends(Long id);

    User removeUserFromFriends(Long id, Long friendId);

    Collection<User> getFriendsInCommon(Long id, Long friendId);
}
