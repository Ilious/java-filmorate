package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.pojo.User;
import ru.yandex.practicum.filmorate.dto.UserRecord;

import java.util.Collection;

public interface IUserService {

    User putUser(UserRecord userRecord);

    User postUser(UserRecord userRecord);

    Collection<User> getAll();
}
