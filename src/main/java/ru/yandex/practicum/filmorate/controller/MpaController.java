package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.service.interfaces.IMpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final IMpaService mpaService;

    public MpaController(IMpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public Collection<MpaDao> getMpas() {
        return mpaService.getMpas();
    }

    @GetMapping("/{mpaId}")
    public MpaDao getMpaById(@PathVariable Long mpaId) {
        return mpaService.getById(mpaId);
    }
}

