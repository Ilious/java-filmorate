package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.storage.interfaces.IDirectorRepo;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
public class DirectorRepo extends BaseRepo<DirectorDao> implements IDirectorRepo{

    private static final String FIND_ALL_QUERY = """
            SELECT * FROM directors
            """;

    private static final String FIND_BY_ID_QUERY = """
            SELECT * FROM directors WHERE id = ?
            """;

    private static final String INSERT_QUERY = """
            INSERT INTO directors(name) VALUES (?)
            """;

    private static final String UPDATE_QUERY = """
            UPDATE directors SET NAME = ?
            WHERE id = ?
            """;

    private static final String DELETE_QUERY = """
            DELETE FROM directors WHERE id = ?
            """;

    private final RowMapper<DirectorDao> mapper;

    public DirectorRepo(JdbcTemplate jdbc, RowMapper<DirectorDao> mapper) {
        super(jdbc);
        this.mapper = mapper;
    }

    @Override
    public DirectorDao createDirector(DirectorDao directorDao) {
        log.trace("DirectorDao.createDirector: directorDao {}", directorDao);

        long id = insert(INSERT_QUERY, directorDao.getName());

        directorDao.setId(id);

        return directorDao;
    }

    @Override
    public Collection<DirectorDao> findAll() {
        log.trace("DirectorDao.findAll: findAll");

        return findMany(FIND_ALL_QUERY, mapper);
    }

    @Override
    public Optional<DirectorDao> findDirectorById(Long id) {
        log.trace("DirectorDao.findDirectorById: by id {}", id);

        return findOne(FIND_BY_ID_QUERY, mapper, id);
    }

    @Override
    public DirectorDao updateDirector(DirectorDao directorDao) {
        log.trace("DirectorDao.updateDirector: directorDao {}", directorDao);

        update(
                UPDATE_QUERY,
                directorDao.getName(),
                directorDao.getId()
        );

        return directorDao;
    }

    @Override
    public void deleteById(Long id) {
        log.trace("DirectorDao.deleteById: by id {}", id);

        delete(DELETE_QUERY, id);
    }
}
