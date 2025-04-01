package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.pojo.FilmRecord;
import ru.yandex.practicum.filmorate.service.IFilmService;

import java.util.Collection;

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
    public ResponseEntity<Film> updateFilm(@RequestBody @Valid @NonNull FilmRecord filmRecord) {
        return ResponseEntity.status(HttpStatus.OK).body(filmService.putFilm(filmRecord));
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@RequestBody @Valid @NonNull FilmRecord filmRecord) {
        return ResponseEntity.status(HttpStatus.CREATED).body(filmService.postFilm(filmRecord));
    }
}
