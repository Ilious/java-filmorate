package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dto.UserRecord;
import ru.yandex.practicum.filmorate.service.interfaces.IUserService;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Collection<UserDao>> getUsers() {
        Collection<UserDao> userDaos = userService.getAll();
        if (userDaos.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(userDaos);
        return ResponseEntity.status(HttpStatus.OK).body(userDaos);
    }

    @PutMapping
    public ResponseEntity<UserDao> updateUser(@RequestBody @Validated(Validator.OnUpdate.class) UserRecord user) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.putUser(user));
    }

    @PostMapping
    public ResponseEntity<UserDao> createUser(
            @RequestBody @Validated(Validator.OnCreate.class) UserRecord userRecord
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.postUser(userRecord));
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}/friends/{friendId}")
    public void putFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeUserFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Collection<UserDao>> getFriends(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getFriends(id));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Collection<UserDao>> getFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getFriendsInCommon(id, otherId));
    }
}
