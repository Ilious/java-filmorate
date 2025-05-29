package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.storage.interfaces.IDirectorRepo;

import java.util.Collection;
import java.util.Optional;

@Repository
public class DirectorRepo extends BaseRepo<DirectorDao> implements IDirectorRepo {

    private static final String FIND_ALL_QUERY = "SELECT * FROM directors";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO directors (name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM directors WHERE id = ?";

    public DirectorRepo(JdbcTemplate jdbc, RowMapper<DirectorDao> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<DirectorDao> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public DirectorDao createDirector(DirectorDao directorDao) {
        long id = insert(INSERT_QUERY, directorDao.getName());
        directorDao.setId(id);
        return directorDao;
    }

    @Override
    public DirectorDao updateDirector(DirectorDao directorDao) {
        update(UPDATE_QUERY,
                directorDao.getName(),
                directorDao.getId());
        return directorDao;
    }

    @Override
    public Optional<DirectorDao> findDirectorById(Long id) {

        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public void deleteDirector(Long id) {
        delete(DELETE_QUERY, id);
    }
}