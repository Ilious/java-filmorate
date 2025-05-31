package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dto.FilmRecord;
import ru.yandex.practicum.filmorate.service.interfaces.IFilmService;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.Collection;
import java.util.List;

@Validated
@RestController
@RequestMapping("/films")
public class FilmController {

    private final IFilmService filmService;

    public FilmController(IFilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<FilmDao> getFilms() {
        return filmService.getAll();
    }

    @GetMapping("/{filmId}")
    public FilmDao getFilms(@PathVariable Long filmId) {
        return filmService.getById(filmId);
    }

    @PutMapping
    public FilmDao updateFilm(
            @RequestBody @NotNull @Validated(Validator.OnUpdate.class) FilmRecord filmRecord
    ) {
        return filmService.putFilm(filmRecord);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDao createFilm(
            @RequestBody @NotNull @Validated(Validator.OnCreate.class) FilmRecord filmRecord
    ) {
        return filmService.postFilm(filmRecord);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putLikeOnFilm(@PathVariable Long id,
                              @PathVariable Long userId) {
        filmService.setLikeOnFilm(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeOnFilm(@PathVariable Long id,
                                 @PathVariable Long userId) {
        filmService.deleteLikeOnFilm(userId, id);
    }

    @GetMapping("/popular")
    public Collection<FilmDao> getPopularFilms(@RequestParam(defaultValue = "10")
                                                               @Positive(message = "count should be greater than 0")
                                                               Long count,
                                                               @RequestParam(required = false)
                                                               @Positive(message = "Genre ID must be a positive number")
                                                               Long genreId,
                                                               @RequestParam(required = false)
                                                               @Min(value = 1895, message = "Year must be at least 1895")
                                                               Integer year) {
        return filmService.getMostLikedFilms(count, genreId, year);
    }

    @DeleteMapping("{filmId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable Long filmId) {
        filmService.deleteFilm(filmId);
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDao> getFilmsByDirector(
            @PathVariable Long directorId,
            @RequestParam(defaultValue = "year")
            @Pattern(regexp = "year|likes", message = "Invalid sortBy parameter")
            String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);

    }

    @GetMapping("/common")
    public Collection<FilmDao> showCommonFilms(@RequestParam(name = "userId") Long userId,
                                                               @RequestParam(name = "friendId") Long friendId) {
        return filmService.showCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public Collection<FilmDao> searchFilm(
            @RequestParam String query,
            @RequestParam String[] by
    ) {
        return filmService.search(query, by);
    }
}
