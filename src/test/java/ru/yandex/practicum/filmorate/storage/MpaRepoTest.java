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
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.enums.AgeRating;
import ru.yandex.practicum.filmorate.storage.mapper.MpaMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;


@JdbcTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@Import(MpaMapper.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaRepoTest {

    @Autowired
    private MpaMapper mapper;

    @Autowired
    private JdbcTemplate jdbc;

    private MpaRepo mpaRepo;

    @BeforeEach
    void init() {
        mpaRepo = new MpaRepo(
                jdbc,
                mapper
        );
    }

    @Test
    void findByIdTest() {
        AgeRating[] values = AgeRating.values();
        List<Optional<MpaDao>> daos = new ArrayList<>();
        for (long i = 0; i < values.length; ++i) {
            daos.add(mpaRepo.findById(i + 1));
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
        List<MpaDao> list = mpaRepo.findAll();

        assertAll(() -> {
            assertFalse(list.isEmpty());
            assertEquals(5, list.size());
        });
    }
}
