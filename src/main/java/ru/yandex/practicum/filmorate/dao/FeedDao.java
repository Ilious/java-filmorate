package ru.yandex.practicum.filmorate.dao;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.service.enums.EntityType;
import ru.yandex.practicum.filmorate.service.enums.Operation;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedDao {

    private Long id;

    private EntityType eventType;

    private Operation operation;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Instant timestamp;

    private Long userId;

    private Long entityId;
}
