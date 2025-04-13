package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.pojo.User;

import java.util.Collection;

public interface IUserRepo {

    Collection<User> getAll();

    User createUser(User user);

    User updateUser(User user);

    User getUserById(Long id);
}
