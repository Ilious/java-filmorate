package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDao {

    private Long reviewId;

    private String content;

    private Boolean isPositive;

    private Long userId;

    private Long filmId;

    private Integer useful;
}
