package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmRecord;
import ru.yandex.practicum.filmorate.pojo.Film;
import ru.yandex.practicum.filmorate.service.interfaces.IFilmService;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/films")
public class FilmController {

    private final IFilmService filmService;

    public FilmController(IFilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public ResponseEntity<Collection<Film>> getFilms() {
        Collection<Film> films = filmService.getAll();
        if (films.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(films);
        return ResponseEntity.status(HttpStatus.OK).body(films);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody @Valid FilmRecord filmRecord) {
        return ResponseEntity.status(HttpStatus.OK).body(filmService.putFilm(filmRecord));
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@RequestBody @Valid FilmRecord filmRecord) {
        return ResponseEntity.status(HttpStatus.CREATED).body(filmService.postFilm(filmRecord));
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> putLikeOnFilm(@PathVariable Long id,
                                              @PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(filmService.setLikeOnFilm(userId, id));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> deleteLikeOnFilm(@PathVariable Long id,
                                                 @PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(filmService.deleteLikeOnFilm(userId, id));
    }

    @GetMapping("/popular")
    public ResponseEntity<Collection<Film>> getPopularFilms(@RequestParam(defaultValue = "10")
                                                            @Positive(message = "count should be greater than 0")
                                                            Long count) {
        return ResponseEntity.status(HttpStatus.OK).body(filmService.getMostLikedFilms(count));
    }
}
