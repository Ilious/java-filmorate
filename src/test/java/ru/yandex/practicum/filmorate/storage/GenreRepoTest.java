package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.enums.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@Import(GenreMapper.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreRepoTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private GenreMapper mapper;

    private GenreRepo genreRepo;

    @BeforeEach
    void init() {
        genreRepo = new GenreRepo(
                jdbc,
                mapper
        );
    }

    @Test
    void findByIdTest() {
        Genre[] values = Genre.values();
        List<Optional<GenreDao>> daos = new ArrayList<>();
        for (long i = 0; i < values.length; ++i) {
            daos.add(genreRepo.findById(i + 1));
        }

        IntStream.range(0, values.length).forEach(idx ->
                assertAll(() -> {
                    assertTrue(daos.get(idx).isPresent());
                    assertEquals(
                            values[idx].getValue(),
                            daos.get(idx).get().getName().getValue()
                    );
                })
        );
    }

    @Test
    void findAllTest() {
        Collection<GenreDao> list = genreRepo.findAll();

        assertAll(() -> {
            assertFalse(list.isEmpty());
            assertEquals(6, list.size());
        });
    }
}