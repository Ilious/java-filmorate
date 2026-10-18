package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.storage.interfaces.IMpaRepo;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaRepo extends BaseRepo<MpaDao> implements IMpaRepo {

    public static final String FIND_BY_ID_QUERY = "SELECT * FROM ratings WHERE id = ?";

    public static final String FIND_ALL_QUERY = "SELECT * FROM ratings ORDER BY id";

    private final RowMapper<MpaDao> mapper;

    public MpaRepo(JdbcTemplate jdbc, RowMapper<MpaDao> mapper) {
        super(jdbc);
        this.mapper = mapper;
    }

    @Override
    public Optional<MpaDao> findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, mapper, id);
    }

    @Override
    public List<MpaDao> findAll() {
        return findMany(FIND_ALL_QUERY, mapper);
    }
}
