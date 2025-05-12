package ru.yandex.practicum.filmorate.storage.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.enums.AgeRating;
import ru.yandex.practicum.filmorate.dao.enums.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SingleFilmExtractor implements ResultSetExtractor<FilmDao> {

    private final GenreMapper genreMapper;

    @Override
    public FilmDao extractData(ResultSet rs) throws SQLException, DataAccessException {

        if (!rs.next())
            return null;

        FilmDao filmDao = null;
        Map<Long, GenreDao> genres = new HashMap<>();
        long filmId = rs.getLong("film_id");

        if (filmDao == null) {
            String name = rs.getString("name");
            String description = rs.getString("description");
            int duration = rs.getInt("duration");
            Long rating = rs.getLong("rating_id");

            Timestamp date = rs.getTimestamp("release_date");
            LocalDate localDate = date.toLocalDateTime().toLocalDate();

            MpaDao mpaDao = new MpaDao();
            mpaDao.setId(rating);
            mpaDao.setName(AgeRating.fromValue(rating));

            processRow(genres, rs);

            while (rs.next()) {
                processRow(genres, rs);
            }

            filmDao = new FilmDao().builder()
                    .id(filmId)
                    .name(name)
                    .description(description)
                    .duration(duration)
                    .releaseDate(localDate)
                    .mpa(mpaDao)
                    .genres(new ArrayList<>(genres.values()))
                    .build();
        }

        return filmDao;
    }

    private void processRow(Map<Long, GenreDao> map, ResultSet rs) throws SQLException {
        Long genreId = rs.getLong("genre_id");
        String genreName = rs.getString("genre_name");

        if (genreId != 0 && genreName != null && !genreName.isEmpty()) {
            Genre genreEnum = Genre.fromValue(genreName);
            GenreDao genreDao = new GenreDao(genreId, genreEnum);
            map.put(genreId, genreDao);
        }
    }
}
