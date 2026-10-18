package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.dao.FeedDao;

import java.util.Collection;

public interface IFeedRepo {

    Collection<FeedDao> findAll();

    void createFeed(FeedDao feed);

    Collection<FeedDao> getByUserId(Long userId);
}
