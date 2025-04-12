package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.pojo.User;
import ru.yandex.practicum.filmorate.dto.UserRecord;
import ru.yandex.practicum.filmorate.service.interfaces.IUserService;
import ru.yandex.practicum.filmorate.storage.interfaces.IUserRepo;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService implements IUserService {

    private final IUserRepo userRepo;

    public UserService(IUserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public User postUser(UserRecord user) {
        User createdUser = User.builder()
                .name(getName(user.name(), user.login()))
                .email(user.email())
                .login(user.login())
                .birthday(user.birthday())
                .build();

        return userRepo.createUser(createdUser);
    }

    @Override
    public User putUser(UserRecord user) {
        if (user.id() == null || user.login().contains(" ")) {
            log.warn("updateUser: Id is not correct");
            throw new ValidationException("updateUser: Id is not correct", "Id", null);
        }

        User userById = userRepo.getUserById(user.id());
        log.debug("updateUser {} {}", user.id(), user.login());


        if (user.birthday() != null)
            userById.setBirthday(user.birthday());

        if (!user.login().isBlank())
            userById.setLogin(user.login());

        userById.setName(getName(user.name(), user.login()));

        if (user.email() != null && !user.email().isBlank())
            userById.setEmail(user.email());

        return userRepo.updateUser(userById);
    }

    @Override
    public Collection<User> getAll() {
        Collection<User> users = userRepo.getAll();
        log.debug("Get user collection {}", users.size());
        return users;
    }

    @Override
    public User addFriend(Long id, Long friendId) {
        User userById = userRepo.getUserById(id);

        User friendById = userRepo.getUserById(friendId);

        friendById.getFriends().add(userById.getId());
        userById.getFriends().add(friendById.getId());
        return friendById;
    }

    @Override
    public User removeUserFromFriends(Long id, Long friendId) {
        User userById = userRepo.getUserById(id);

        User friendById = userRepo.getUserById(friendId);

        friendById.getFriends().remove(userById.getId());
        userById.getFriends().remove(friendById.getId());
        return friendById;
    }

    @Override
    public Collection<User> getFriends(Long id) {
        Set<Long> friendsIds = userRepo.getUserById(id).getFriends();

        return friendsIds.stream()
                .map(friendId -> {
                            try {
                                return userRepo.getUserById(friendId);
                            } catch (EntityNotFoundException ex) {
                                log.warn("User {} get Friend wasn't found id: {}", id, friendId);
                                return null;
                            }
                        }
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getFriendsInCommon(Long id, Long friendId) {
        Collection<User> userFriends = getFriends(id);

        Collection<User> anotherUserFriends = getFriends(friendId);

        return userFriends.stream()
                .filter(anotherUserFriends::contains)
                .collect(Collectors.toList());
    }

    private String getName(String name, String login) {
        return Objects.nonNull(name) && !name.isBlank() ? name : login;
    }
}
