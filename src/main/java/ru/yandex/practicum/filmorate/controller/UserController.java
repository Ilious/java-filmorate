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
}
