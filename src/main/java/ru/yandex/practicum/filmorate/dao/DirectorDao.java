package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectorDao implements HasId {

    private Long id;

    private String name;
}
