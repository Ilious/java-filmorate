package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dto.UserRecord;
import ru.yandex.practicum.filmorate.service.interfaces.IFilmService;
import ru.yandex.practicum.filmorate.service.interfaces.IUserService;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    private final IFilmService filmService;

    @GetMapping
    public Collection<UserDao> getUsers() {
        return userService.getAll();
    }

    @PutMapping
    public UserDao updateUser(@RequestBody @Validated(Validator.OnUpdate.class) UserRecord user) {
        return userService.putUser(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDao createUser(
            @RequestBody @Validated(Validator.OnCreate.class) UserRecord userRecord
    ) {
        return userService.postUser(userRecord);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeUserFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDao> getFriends(@PathVariable Long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDao> getFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getFriendsInCommon(id, otherId);
    }

    @GetMapping("/{id}/feed")
    public Collection<FeedDao> getFeed(@PathVariable Long id) {
        return userService.getFeed(id);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/{id}")
    public UserDao getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }


    @GetMapping("/{id}/recommendations")
    public Collection<FilmDao> getRecommendations(@PathVariable("id") Long userId) {
        return filmService.getRecommendations(userId);
    }
}
