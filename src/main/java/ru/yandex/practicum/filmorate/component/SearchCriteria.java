package ru.yandex.practicum.filmorate.component;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SearchCriteria {
    private final String query;

    private final Object[] params;

    public SearchCriteria(String query, Object... params) {
        this.params = params;
        this.query = query;
    }
}
