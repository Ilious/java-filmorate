package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.service.enums.EntityType;
import ru.yandex.practicum.filmorate.service.enums.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

@Component
public class FeedMapper implements RowMapper<FeedDao> {

    @Override
    public FeedDao mapRow(ResultSet rs, int rowNum) throws SQLException {

        Long eventId = rs.getLong("event_id");
        long userId = rs.getLong("user_id");
        long entityId = rs.getLong("entity_id");
        String operation = rs.getString("operation");
        String eventType = rs.getString("event_type");

        Timestamp timestamp = rs.getTimestamp("timestamp");
        Instant instant = timestamp.toInstant();

        return FeedDao.builder()
                .id(eventId)
                .userId(userId)
                .entityId(entityId)
                .eventType(EntityType.fromValue(eventType))
                .operation(Operation.fromValue(operation))
                .timestamp(instant)
                .build();
    }
}
