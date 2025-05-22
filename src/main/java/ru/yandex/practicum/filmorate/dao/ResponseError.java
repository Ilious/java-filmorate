package ru.yandex.practicum.filmorate.dao;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseError {

    private final int code;

    private final String description;
}
