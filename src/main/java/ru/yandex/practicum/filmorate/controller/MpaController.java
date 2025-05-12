package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.service.interfaces.IMpaService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final IMpaService mpaService;

    public MpaController(IMpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public ResponseEntity<Collection<MpaDao>> getMpas(@PathVariable Optional<Long> mpaId) {
        return ResponseEntity.status(HttpStatus.OK).body(mpaService.getMpas());
    }

    @GetMapping("/{mpaId}")
    public ResponseEntity<MpaDao> getMpaById(@PathVariable Long mpaId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(mpaService.getById(mpaId));
    }
}

