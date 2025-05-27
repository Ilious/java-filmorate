package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dto.FilmRecord;

import java.util.Collection;
import java.util.Optional;

public interface IFeedRepo {

    Collection<FeedDao> findAll();

    void createFeed(FeedDao feed);
}
