package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.enums.AgeRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;


@Component
public class FilmMapper implements RowMapper<FilmDao> {

    @Override
    public FilmDao mapRow(ResultSet rs, int rowNum) throws SQLException {

        long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        int duration = rs.getInt("duration");
        Long rating = rs.getLong("rating_id");

        Timestamp releaseDate = rs.getTimestamp("release_date");
        LocalDate localDate = releaseDate.toLocalDateTime().toLocalDate();

        MpaDao mpa = new MpaDao();
        mpa.setId(rating);
        mpa.setName(AgeRating.fromValue(rating));

        return new FilmDao(id, name, description, localDate, duration, mpa, new ArrayList<>());
    }
}
