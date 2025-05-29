package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dto.DirectorRecord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DirectorMapper implements RowMapper<DirectorDao> {

    @Override
    public DirectorDao mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");

        return new DirectorDao(id, name);
    }

    public static List<DirectorDao> toDirectorsDaos(List<DirectorRecord> directorRecords) {
        List<DirectorDao> directors = new ArrayList<>();
        if (directorRecords != null) {
            directors = directorRecords.stream()
                    .map(req -> new DirectorDao(req.id(), req.name()))
                    .toList();
        }
        return directors;
    }
}
