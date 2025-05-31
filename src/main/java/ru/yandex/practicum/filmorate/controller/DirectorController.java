package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dto.DirectorRecord;
import ru.yandex.practicum.filmorate.service.interfaces.IDirectorService;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/directors")
public class DirectorController {

    private final IDirectorService directorService;

    public DirectorController(IDirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public Collection<DirectorDao> getAllDirectors() {
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public DirectorDao getDirectorById(@PathVariable Long id) {
        return directorService.getDirectorById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorDao createDirector(
            @RequestBody @Validated(Validator.OnCreate.class) DirectorRecord directorRecord) {
        return directorService.postDirector(directorRecord);
    }

    @PutMapping
    public DirectorDao updateDirector(
            @RequestBody @Validated(Validator.OnUpdate.class) DirectorRecord directorRecord) {
        return directorService.putDirector(directorRecord);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDirector(@PathVariable Long id) {
        directorService.deleteDirector(id);
    }
}