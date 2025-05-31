package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dao.enums.Genre;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenreDao implements HasId {

    private Long id;

    private Genre name;
}
