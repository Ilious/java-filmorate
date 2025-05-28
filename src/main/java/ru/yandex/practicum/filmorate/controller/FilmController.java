package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Collection<FilmDao>> getFilms() {
        Collection<FilmDao> filmDaos = filmService.getAll();
        if (filmDaos.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(filmDaos);
        return ResponseEntity.status(HttpStatus.OK).body(filmDaos);
    }

    @GetMapping("/{filmId}")
    public ResponseEntity<FilmDao> getFilms(@PathVariable Long filmId) {
        return ResponseEntity.status(HttpStatus.OK).body(filmService.getById(filmId));
    }

    @PutMapping
    public ResponseEntity<FilmDao> updateFilm(@RequestBody
                                              @NotNull
                                              @Validated(Validator.OnUpdate.class) FilmRecord filmRecord) {
        return ResponseEntity.status(HttpStatus.OK).body(filmService.putFilm(filmRecord));
    }

    @PostMapping
    public ResponseEntity<FilmDao> createFilm(@RequestBody
                                              @NotNull
                                              @Validated(Validator.OnCreate.class) FilmRecord filmRecord) {
        return ResponseEntity.status(HttpStatus.CREATED).body(filmService.postFilm(filmRecord));
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}/like/{userId}")
    public void putLikeOnFilm(@PathVariable Long id,
                              @PathVariable Long userId) {
        filmService.setLikeOnFilm(userId, id);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeOnFilm(@PathVariable Long id,
                                 @PathVariable Long userId) {
        filmService.deleteLikeOnFilm(userId, id);
    }

    @GetMapping("/popular")
    public ResponseEntity<Collection<FilmDao>> getPopularFilms(@RequestParam(defaultValue = "10")
                                                               @Positive(message = "count should be greater than 0")
                                                               Long count,
                                                               @RequestParam(required = false)
                                                               @Positive(message = "Genre ID must be a positive number")
                                                               Long genreId,
                                                               @RequestParam(required = false)
                                                               @Min(value = 1895, message = "Year must be at least 1895")
                                                               Integer year) {
        return ResponseEntity.status(HttpStatus.OK).body(filmService.getMostLikedFilms(count, genreId, year));
    }

    @GetMapping("/director/{directorId}")
    public ResponseEntity<List<FilmDao>> getFilmsByDirector(
            @PathVariable Long directorId,
            @RequestParam(defaultValue = "year")
            @Pattern(regexp = "year|likes", message = "Invalid sortBy parameter")
            String sortBy) {
        return ResponseEntity.ok(filmService.getFilmsByDirector(directorId, sortBy));
    }
}
