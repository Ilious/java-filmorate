package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.dto.FeedRecord;
import ru.yandex.practicum.filmorate.mapper.FeedMapper;
import ru.yandex.practicum.filmorate.service.interfaces.IFeedService;
import ru.yandex.practicum.filmorate.storage.interfaces.IFeedRepo;

import java.util.Collection;

@Slf4j
@Service
public class FeedService implements IFeedService {

    private final IFeedRepo feedRepo;

    public FeedService(IFeedRepo feedRepo) {
        this.feedRepo = feedRepo;
    }

    @Override
    public Collection<FeedDao> getAll() {
        return feedRepo.findAll();
    }

    @Override
    public void postFeed(FeedRecord feed) {
        FeedDao feedDao = FeedMapper.toFeedDao(feed);

        feedRepo.createFeed(feedDao);
    }

    @Override
    public Collection<FeedDao> getByUserId(Long userId) {
        return feedRepo.getByUserId(userId);
    }
}
