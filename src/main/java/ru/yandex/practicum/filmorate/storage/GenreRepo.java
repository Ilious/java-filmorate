package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.interfaces.IGenreRepo;

import java.util.Collection;
import java.util.Optional;

@Repository
public class GenreRepo extends BaseRepo<GenreDao> implements IGenreRepo {

    public static final String GET_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";

    public static final String GET_ALL_QUERY = "SELECT * FROM genres ORDER BY id";

    public GenreRepo(JdbcTemplate jdbc, RowMapper<GenreDao> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<GenreDao> findById(Long id) {
        return findOne(GET_BY_ID_QUERY, id);
    }

    @Override
    public Collection<GenreDao> findAll() {
        return findMany(GET_ALL_QUERY);
    }
}
