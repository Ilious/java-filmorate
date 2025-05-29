package ru.yandex.practicum.filmorate.dto;

import ru.yandex.practicum.filmorate.service.enums.EntityType;
import ru.yandex.practicum.filmorate.service.enums.Operation;

public record FeedRecord(
        Long userId,
        Long entityId,
        EntityType type,
        Operation operation
) {
}
