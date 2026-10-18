package ru.yandex.practicum.filmorate.controller;

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
    public Collection<GenreDao> getAll() {
        return genreService.getAll();
    }

    @GetMapping("/{genreId}")
    public GenreDao getById(@PathVariable Long genreId) {
        return genreService.getById(genreId);
    }
}

