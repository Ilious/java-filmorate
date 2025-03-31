package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityExistsException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class UserRepo implements IUserRepo {

    private final Map<Long, User> storage = new HashMap<>();

    @Override
    public Collection<User> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public User createUser(Long id, User user) {
        if (storage.containsKey(user.getId())) {
            log.warn("User exists by id {}", user.getId());
            throw new EntityExistsException("User exists by id %d".formatted(user.getId()));
        }
        storage.put(id, user);

        return user;
    }

    @Override
    public User updateUser(Long id, User user) {
        storage.put(id, user);

        return user;
    }

    @Override
    public User getUserById(Long id) {
        return storage.get(id);
    }
}
