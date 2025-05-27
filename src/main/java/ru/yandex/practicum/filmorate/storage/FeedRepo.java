package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.storage.interfaces.IFeedRepo;

import java.time.Instant;
import java.util.Collection;

@Repository
public class FeedRepo extends BaseRepo<FeedDao> implements IFeedRepo {

    private static final String FIND_ALL_QUERY = """
            SELECT f.id as event_id,
            f.timestamp,
            f.event_type,
            f.operation,
            f.user_id,
            f.event_id,
            FROM feeds f
            """;

    private static final String INSERT_QUERY = """
            INSERT INTO feeds(timestamp, event_type, operation, user_id,  entity_id)
            VALUES(?, ?, ?, ?, ?)
            """;

    public FeedRepo(JdbcTemplate jdbc, RowMapper<FeedDao> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<FeedDao> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public FeedDao createFeed(FeedDao feed) {
        long id = insert(INSERT_QUERY,
                Instant.now(),
                feed.getEventType(),
                feed.getOperation(),
                feed.getUserId(),
                feed.getEntityId());

        feed.setId(id);
        return feed;
    }
}
