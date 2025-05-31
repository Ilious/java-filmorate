package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDao implements HasId {

    private Long id;

    private String email;

    private String login;

    private String name;

    private LocalDate birthday;
}
