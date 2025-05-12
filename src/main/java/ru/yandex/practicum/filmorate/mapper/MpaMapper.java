package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.enums.AgeRating;
import ru.yandex.practicum.filmorate.dto.MpaRecord;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MpaMapper {

    public static MpaDao toMpaDao(MpaRecord req) {
        return new MpaDao(req.id(), AgeRating.fromValue(req.id()));
    }
}
