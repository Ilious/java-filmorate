package ru.yandex.practicum.filmorate.storage.mapper;

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
import java.util.List;
import java.util.Map;

@Component
public class FilmExtractor implements ResultSetExtractor<List<FilmDao>> {

    @Override
    public List<FilmDao> extractData(ResultSet rs) throws SQLException, DataAccessException {

        Map<Long, FilmDao> films = new HashMap<>();
        while (rs.next()) {
            Long id = rs.getLong("film_id");
            FilmDao film = films.get(id);

            if (film == null) {
                String name = rs.getString("name");
                String description = rs.getString("description");
                int duration = rs.getInt("duration");
                Long rating = rs.getLong("rating_id");

                Timestamp releaseDate = rs.getTimestamp("release_date");
                LocalDate localDate = releaseDate.toLocalDateTime().toLocalDate();

                MpaDao mpa = new MpaDao();
                mpa.setId(rating);
                mpa.setName(AgeRating.fromValue(rating));

                film = new FilmDao(id, name, description, localDate, duration, mpa, new ArrayList<>());
                films.put(id, film);
            }

            Long genre_id = rs.getLong("genre_id");
            String genre_name = rs.getString("genre_name");

            if (genre_id != 0 && genre_name != null) {
                GenreDao genreDao = new GenreDao(genre_id, Genre.fromValue(genre_name));
                film.getGenres().add(genreDao);
            }
        }

        return new ArrayList<>(films.values());
    }
}
