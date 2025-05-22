package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.enums.AgeRating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaMapper implements RowMapper<MpaDao> {

    @Override
    public MpaDao mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        String ratingName = rs.getString("name");

        return new MpaDao(id, AgeRating.fromValue(ratingName));
    }
}
