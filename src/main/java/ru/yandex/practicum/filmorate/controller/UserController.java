package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.pojo.User;
import ru.yandex.practicum.filmorate.dto.UserRecord;
import ru.yandex.practicum.filmorate.service.interfaces.IUserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Collection<User>> getUsers() {
        Collection<User> users = userService.getAll();
        if (users.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(users);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody @Valid @NonNull UserRecord user) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.putUser(user));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid @NonNull UserRecord userRecord) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.postUser(userRecord));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> putFriend(@PathVariable Long id,
                                          @PathVariable Long friendId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.addFriend(id, friendId));
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> deleteFriend(@PathVariable Long id,
                                             @PathVariable Long friendId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.removeUserFromFriends(id, friendId));
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Collection<User>> getFriends(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getFriends(id));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Collection<User>> getFriends(@PathVariable Long id,
                                                       @PathVariable Long otherId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getFriendsInCommon(id, otherId));
    }
}
