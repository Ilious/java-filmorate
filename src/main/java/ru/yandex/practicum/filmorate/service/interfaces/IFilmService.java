package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dto.FilmRecord;

import java.util.Collection;

public interface IFilmService {

    FilmDao putFilm(FilmRecord filmRecord);

    FilmDao postFilm(FilmRecord filmRecord);

    Collection<FilmDao> getAll();

    void setLikeOnFilm(Long userId, Long filmId);

    void deleteLikeOnFilm(Long userId, Long filmId);

    Collection<FilmDao> getMostLikedFilms(Long count, Long genreId, Integer year);

    FilmDao getById(Long filmId);

    void deleteFilm(Long filmId);

    Collection<FilmDao> getRecommendations(Long userId);

}
