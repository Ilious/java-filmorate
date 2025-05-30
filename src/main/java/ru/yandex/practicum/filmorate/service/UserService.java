package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dto.UserRecord;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.service.interfaces.IUserService;
import ru.yandex.practicum.filmorate.storage.interfaces.IUserRepo;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepo userRepo;

    @Override
    public UserDao postUser(UserRecord user) {
        loginValidation(user.login());

        UserDao userDao = UserMapper.toUserDao(user);

        return userRepo.createUser(userDao);
    }

    @Override
    public UserDao putUser(UserRecord user) {
        loginValidation(user.login());
        if (user.id() == null) {
            log.warn("updateUser: Id is not correct: id [null]");
            throw new ValidationException("updateUser: Id is not correct", "Id", null);
        }

        UserDao userDao = getUserById(user.id());
        UserMapper.updateFields(userDao, user);

        log.debug("updateUser {} {}", user.id(), user.login());

        return userRepo.updateUser(userDao);
    }

    @Override
    public Collection<UserDao> getAll() {
        Collection<UserDao> userDaos = userRepo.findAll();
        log.debug("Get user collection {}", userDaos.size());
        return userDaos;
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        existsUserOrThrowErr(id);

        existsUserOrThrowErr(friendId);

        userRepo.addFriend(id, friendId);
    }

    @Override
    public void removeUserFromFriends(Long id, Long friendId) {
        existsUserOrThrowErr(id);

        existsUserOrThrowErr(friendId);

        userRepo.removeFromFriends(id, friendId);
    }

    @Override
    public UserDao getUserById(Long id) {
        return userRepo.findUserById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Entity user not found", "User", "id", String.valueOf(id)
                        )
                );
    }

    @Override
    public Collection<UserDao> getFriends(Long id) {
        existsUserOrThrowErr(id);

        return userRepo.findFriends(id);
    }

    @Override
    public Collection<UserDao> getFriendsInCommon(Long id, Long friendId) {
        Collection<UserDao> userDaoFriends = getFriends(id);

        Collection<UserDao> anotherUserDaoFriends = getFriends(friendId);

        return userDaoFriends.stream()
                .filter(anotherUserDaoFriends::contains)
                .collect(Collectors.toList());
    }

    private void loginValidation(String login) {
        if (login.contains(" ")) {
            log.warn("updateUser: Id is not correct: login [{}]", login);
            throw new ValidationException("updateUser: login is not correct {}", "login", login);
        }
    }

    private void existsUserOrThrowErr(Long id) throws EntityNotFoundException {
        userRepo.findUserById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                                "Entity User not found", "User", "Id", String.valueOf(id)
                        )
                );
    }
}
