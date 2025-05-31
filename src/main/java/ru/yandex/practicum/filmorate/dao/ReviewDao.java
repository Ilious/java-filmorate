package ru.yandex.practicum.filmorate.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDao implements HasId {

    @JsonProperty("reviewId")
    private Long id;

    private String content;

    private Boolean isPositive;

    private Long userId;

    private Long filmId;

    private Integer useful;
}
