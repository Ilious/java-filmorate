package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dto.DirectorRecord;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DirectorMapper {

    public static DirectorDao toDirectorDao(DirectorRecord req) {
        return DirectorDao.builder()
                .name(req.name())
                .id(req.id())
                .build();
    }

    public static void updateFields(DirectorDao director, DirectorRecord req) {
        if (req.name() != null && !req.name().isBlank()) {
            director.setName(req.name());
        }
    }
}