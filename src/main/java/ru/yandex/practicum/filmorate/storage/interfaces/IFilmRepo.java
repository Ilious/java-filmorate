package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.dao.FilmDao;

import java.util.Collection;
import java.util.Optional;

public interface IFilmRepo {

    Collection<FilmDao> findAll();

    FilmDao createFilm(FilmDao filmDao);

    FilmDao updateFilm(FilmDao filmDao);

    Optional<FilmDao> findFilmById(Long id);

    void setLikeOnFilm(Long filmId, Long userId);

    void deleteLikeFromFilm(Long filmId, Long userId);

    Collection<FilmDao> findNPopular(Long count);

    Collection<FilmDao> getRecommendations(Long userId);
}
