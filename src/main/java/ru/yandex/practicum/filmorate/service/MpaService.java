package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.service.interfaces.IMpaService;
import ru.yandex.practicum.filmorate.storage.interfaces.IMpaRepo;

import java.util.Collection;

@Slf4j
@Service
public class MpaService implements IMpaService {

    private final IMpaRepo mpaRepo;

    public MpaService(IMpaRepo mpaRepo) {
        this.mpaRepo = mpaRepo;
    }

    @Override
    public Collection<MpaDao> getMpas() {
        return mpaRepo.findAll();
    }

    @Override
    public MpaDao getById(Long id) {
        return mpaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                                "Entity Mpa not found", "Mpa", "id", String.valueOf(id)
                        )
                );
    }

    @Override
    public void validateId(Long id) {
        getById(id);
    }
}
