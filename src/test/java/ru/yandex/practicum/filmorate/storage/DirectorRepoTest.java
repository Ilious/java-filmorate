package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.dao.DirectorDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@Import({DirectorRepoTest.TestConfig.class, DirectorRepo.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DirectorRepoTest {

    @Configuration
    static class TestConfig {
        @Bean
        public RowMapper<DirectorDao> directorRowMapper() {
            return (rs, rowNum) -> DirectorDao.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .build();
        }
    }

    private final JdbcTemplate jdbc;
    private final DirectorRepo directorRepo;

    @Test
    void createDirectorShouldSaveWithGeneratedId() {
        DirectorDao newDirector = DirectorDao.builder().name("New Director").build();

        DirectorDao savedDirector = directorRepo.createDirector(newDirector);

        assertNotNull(savedDirector.getId());
        assertEquals(newDirector.getName(), savedDirector.getName());

        DirectorDao dbDirector = jdbc.queryForObject(
                "SELECT * FROM directors WHERE id = ?",
                (rs, rowNum) -> DirectorDao.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .build(),
                savedDirector.getId()
        );

        assertEquals(savedDirector, dbDirector);
    }

    @Test
    void updateDirectorShouldModifyExistingRecord() {
        DirectorDao original = createTestDirector("Original Name");
        DirectorDao updated = DirectorDao.builder()
                .id(original.getId())
                .name("Updated Name")
                .build();

        directorRepo.updateDirector(updated);

        DirectorDao dbDirector = jdbc.queryForObject(
                "SELECT * FROM directors WHERE id = ?",
                (rs, rowNum) -> DirectorDao.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .build(),
                original.getId()
        );

        assertNotNull(dbDirector);
        assertEquals("Updated Name", dbDirector.getName());
    }

    @Test
    void findDirectorByIdShouldReturnCorrectDirector() {
        DirectorDao expected = createTestDirector("Test Director");

        Optional<DirectorDao> result = directorRepo.findDirectorById(expected.getId());

        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
    }

    @Test
    void findDirectorByIdShouldReturnEmptyForNonExistingId() {
        Optional<DirectorDao> result = directorRepo.findDirectorById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteDirectorShouldRemoveFromDatabase() {
        DirectorDao director = createTestDirector("To Delete");

        directorRepo.deleteDirector(director.getId());

        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM directors WHERE id = ?",
                Integer.class,
                director.getId()
        );
        assertEquals(0, count);
    }

    private DirectorDao createTestDirector(String name) {
        DirectorDao director = DirectorDao.builder().name(name).build();
        return directorRepo.createDirector(director);
    }
}