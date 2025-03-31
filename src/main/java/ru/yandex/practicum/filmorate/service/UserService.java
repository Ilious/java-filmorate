package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.pojo.UserRecord;
import ru.yandex.practicum.filmorate.storage.IUserRepo;

import java.util.Collection;
import java.util.Objects;

@Slf4j
@Service
public class UserService implements IUserService {

    private final IUserRepo userRepo;

    public UserService(IUserRepo userRepo) {
        this.userRepo = userRepo;
    }

    private Long idx = 0L;

    private Long updIdx() {
        return ++idx;
    }

    @Override
    public User postUser(UserRecord user) {
        User createdUser = User.builder()
                .id(updIdx())
                .name(getName(user.name(), user.login()))
                .email(user.email())
                .login(user.login())
                .birthday(user.birthday())
                .build();

        log.debug("postUser {} {}", createdUser.getId(), createdUser.getLogin());

        return userRepo.createUser(createdUser.getId(), createdUser);
    }

    @Override
    public User putUser(UserRecord user) {
        if (user.id() == null || user.login().contains(" ")) {
            log.warn("updateUser: Id is not correct");
            throw new ValidationException("updateUser: Id is not correct");
        }

        User userById = userRepo.getUserById(user.id());
        log.debug("updateUser {} {}", user.id(), user.login());


        if (user.birthday() != null)
            userById.setBirthday(user.birthday());

        if (user.login() != null && !user.login().isBlank())
            userById.setLogin(user.login());

        userById.setName(getName(user.name(), user.login()));

        if (user.email() != null && !user.email().isBlank())
            userById.setEmail(user.email());

        return userRepo.updateUser(user.id(), userById);
    }

    @Override
    public Collection<User> getAll() {
        Collection<User> users = userRepo.getAll();
        log.debug("Get user collection {}", users.size());
        return users;
    }

    private String getName(String name, String login) {
        return Objects.nonNull(name) && !name.isBlank() ? name : login;
    }
}
