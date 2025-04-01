package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.pojo.UserRecord;

import java.util.Collection;

public interface IUserService {

    User putUser(UserRecord userRecord);

    User postUser(UserRecord userRecord);

    Collection<User> getAll();
}
