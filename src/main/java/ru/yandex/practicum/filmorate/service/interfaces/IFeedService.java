package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.dto.FeedRecord;

import java.util.Collection;

public interface IFeedService {

    Collection<FeedDao> getAll();

    void postFeed(FeedRecord feed);
}
