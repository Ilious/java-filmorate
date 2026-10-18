package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.dto.FeedRecord;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedMapper {

    public static FeedDao toFeedDao(FeedRecord req) {
        return FeedDao.builder()
                .userId(req.userId())
                .entityId(req.entityId())
                .eventType(req.type())
                .operation(req.operation())
                .timestamp(Instant.now())
                .build();
    }
}
