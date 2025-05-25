package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Collection<DirectorDao>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(directorService.getDirectors());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DirectorDao getById(@PathVariable Long id) {
        return directorService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorDao createDirector(@RequestBody @Validated(Validator.OnCreate.class) DirectorRecord directorRecord) {
        return directorService.postDirector(directorRecord);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorDao updateDirector(@RequestBody @Validated(Validator.OnUpdate.class) DirectorRecord directorRecord) {
        return directorService.putDirector(directorRecord);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable Long id) {
        directorService.getById(id);
    }
}
