package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.storage.interfaces.IFeedRepo;

import java.sql.Timestamp;
import java.util.Collection;

@Repository
public class FeedRepo extends BaseRepo<FeedDao> implements IFeedRepo {

    private static final String FIND_ALL_QUERY = """
            SELECT f.id as event_id,
            f.timestamp,
            f.event_type,
            f.entity_id,
            f.operation,
            f.user_id,
            FROM feeds f
            """;

    private static final String FIND_BY_USER_ID_QUERY = """
            SELECT f.id as event_id,
            f.timestamp,
            f.event_type,
            f.entity_id,
            f.operation,
            f.user_id,
            FROM feeds f
            WHERE f.user_id = ?
            ORDER BY event_id ASC
            """;


    private static final String INSERT_QUERY = """
            INSERT INTO feeds(timestamp, event_type, operation, user_id,  entity_id)
            VALUES(?, ?, ?, ?, ?)
            """;

    private final RowMapper<FeedDao> mapper;

    public FeedRepo(JdbcTemplate jdbc, RowMapper<FeedDao> mapper) {
        super(jdbc);
        this.mapper = mapper;
    }

    @Override
    public Collection<FeedDao> findAll() {
        return findMany(FIND_ALL_QUERY, mapper);
    }

    @Override
    public void createFeed(FeedDao feed) {
        long id = insert(INSERT_QUERY,
                Timestamp.from(feed.getTimestamp()),
                feed.getEventType().getValue(),
                feed.getOperation().getValue(),
                feed.getUserId(),
                feed.getEntityId());

        feed.setId(id);
    }

    @Override
    public Collection<FeedDao> getByUserId(Long userId) {
        return findMany(FIND_BY_USER_ID_QUERY, mapper, userId);
    }
}
