package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorDao;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DirectorMapper implements RowMapper<DirectorDao> {

    @Override
    public DirectorDao mapRow(ResultSet rs, int rowNum) throws SQLException {
        return DirectorDao.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }
}