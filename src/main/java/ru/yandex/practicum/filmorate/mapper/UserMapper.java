package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dto.UserRecord;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserDao toUserDao(UserRecord req) {
        UserDao user = UserDao.builder()
                .name(getName(req.name(), req.login()))
                .email(req.email())
                .login(req.login())
                .birthday(req.birthday())
                .build();
        return user;
    }

    public static void updateFields(UserDao user, UserRecord req) {
        if (req.birthday() != null)
            user.setBirthday(req.birthday());

        if (!req.login().isBlank())
            user.setLogin(req.login());

        user.setName(getName(req.name(), req.login()));

        if (req.email() != null && !req.email().isBlank())
            user.setEmail(req.email());
    }

    private static String getName(String name, String login) {
        return Objects.nonNull(name) && !name.isBlank() ? name : login;
    }
}
