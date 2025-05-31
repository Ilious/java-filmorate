package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseRepo<T> {

    protected final JdbcTemplate jdbc;

    protected Optional<T> findOne(String query, RowMapper<T> mapper, Object... params) {
        try {
            T result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (DataAccessException ex) {
            log.warn("BaseRepo.findOne: data is empty");
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, RowMapper<T> mapper, Object... params) {
        return jdbc.query(query, mapper, params);
    }

    protected boolean delete(String query, Object... params) {
        int rowsDeleted = jdbc.update(query, params);
        return rowsDeleted > 0;
    }

    protected void update(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0)
            throw new InternalServerException("Data isn't updated");
    }

    protected long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement st = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int i = 1; i <= params.length; ++i)
                st.setObject(i, params[i - 1]);
            return st;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);

        if (id != null)
            return id;
        else
            throw new InternalServerException("Data isn't updated");
    }

    protected void insertNoKey(String query, Object... params) {
        jdbc.update(connection -> {
            PreparedStatement st = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int i = 1; i <= params.length; ++i)
                st.setObject(i, params[i - 1]);
            return st;
        });
    }

    protected <T> void insertBatch(String query, List<T> objects, BiConsumer<PreparedStatement, T> setter) {
        jdbc.batchUpdate(query, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) {
                setter.accept(ps, objects.get(i));
            }

            @Override
            public int getBatchSize() {
                return objects.size();
            }
        });
    }

    protected <R> R extract(String query, ResultSetExtractor<R> rs, Object... params) {
        return jdbc.query(query, rs, params);
    }
}
