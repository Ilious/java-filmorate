package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dao.enums.AgeRating;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MpaDao implements HasId {

    private Long id;

    private AgeRating name;
}
