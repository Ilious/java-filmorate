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
    public ResponseEntity<Collection<DirectorDao>> getAllDirectors() {
        Collection<DirectorDao> directors = directorService.getAllDirectors();
        if (directors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(directors);
        }
        return ResponseEntity.ok(directors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DirectorDao> getDirectorById(@PathVariable Long id) {
        return ResponseEntity.ok(directorService.getDirectorById(id));
    }

    @PostMapping
    public ResponseEntity<DirectorDao> createDirector(
            @RequestBody @Validated(Validator.OnCreate.class) DirectorRecord directorRecord) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(directorService.postDirector(directorRecord));
    }

    @PutMapping
    public ResponseEntity<DirectorDao> updateDirector(
            @RequestBody @Validated(Validator.OnUpdate.class) DirectorRecord directorRecord) {
        return ResponseEntity.ok(directorService.putDirector(directorRecord));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDirector(@PathVariable Long id) {
        directorService.deleteDirector(id);
    }
}