package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.service.interfaces.IGenreService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
public class GenreController {

    private final IGenreService genreService;

    public GenreController(IGenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public ResponseEntity<Collection<GenreDao>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(genreService.getAll());
    }

    @GetMapping("/{genreId}")
    public ResponseEntity<GenreDao> getById(@PathVariable Long genreId) {
        return ResponseEntity.status(HttpStatus.OK).body(genreService.getById(genreId));
    }
}

