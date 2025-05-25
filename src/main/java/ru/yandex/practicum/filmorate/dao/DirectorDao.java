package ru.yandex.practicum.filmorate.dao;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.DirectorRecord;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectorDao {

    private Long id;

    private String name;
}
