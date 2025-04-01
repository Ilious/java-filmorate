package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class UserRepo implements IUserRepo {

    private final Map<Long, User> storage = new HashMap<>();

    private Long idx = 0L;

    private Long updIdx() {
        return ++idx;
    }

    @Override
    public Collection<User> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public User createUser(User user) {
        user.setId(updIdx());
        log.debug("postUser {} {}", user.getId(), user.getLogin());

        storage.put(user.getId(), user);

        return user;
    }

    @Override
    public User updateUser(User user) {
        storage.put(user.getId(), user);

        return user;
    }

    @Override
    public User getUserById(Long id) {
        return storage.get(id);
    }
}
