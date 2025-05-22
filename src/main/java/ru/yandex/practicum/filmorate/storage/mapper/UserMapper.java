package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

@Component
public class UserMapper implements RowMapper<UserDao> {


    @Override
    public UserDao mapRow(ResultSet rs, int rowNum) throws SQLException {

        long id = rs.getInt("id");
        String name = rs.getString("name");
        String login = rs.getString("login");
        String email = rs.getString("email");

        Timestamp birthday = rs.getTimestamp("birthday");
        LocalDate localDate = birthday.toLocalDateTime().toLocalDate();

        return new UserDao(id, email, login, name, localDate);
    }
}
