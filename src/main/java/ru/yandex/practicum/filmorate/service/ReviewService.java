package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dto.FeedRecord;
import ru.yandex.practicum.filmorate.dto.ReviewRecord;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.service.enums.EntityType;
import ru.yandex.practicum.filmorate.service.enums.LikeOnReviewActions;
import ru.yandex.practicum.filmorate.service.enums.Operation;
import ru.yandex.practicum.filmorate.service.interfaces.IFeedService;
import ru.yandex.practicum.filmorate.service.interfaces.IReviewService;
import ru.yandex.practicum.filmorate.storage.FilmRepo;
import ru.yandex.practicum.filmorate.storage.ReviewRepo;
import ru.yandex.practicum.filmorate.storage.UserRepo;

import java.util.Collection;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {

    private final ReviewRepo reviewRepo;

    private final UserRepo userRepo;

    private final FilmRepo filmRepo;

    private final IFeedService feedService;

    @Override
    public ReviewDao postReview(ReviewRecord reviewRecord) {
        userRepo.findUserById(reviewRecord.userId()).orElseThrow(() -> new EntityNotFoundException(
                "Entity user not found", "User", "id", String.valueOf(reviewRecord.userId())));

        filmRepo.findFilmById(reviewRecord.filmId()).orElseThrow(() -> new EntityNotFoundException(
                "Entity Film not found", "Film", "Id", String.valueOf(reviewRecord.filmId())));

        ReviewDao reviewDao = ReviewMapper.toReviewDao(reviewRecord);

        ReviewDao postedReview = reviewRepo.postReview(reviewDao);

        feedService.postFeed(
                new FeedRecord(reviewRecord.userId(), postedReview.getReviewId(), EntityType.REVIEW, Operation.ADD)
        );

        return postedReview;
    }

    @Override
    public ReviewDao putReview(ReviewRecord reviewRecord) {
        getReviewById(reviewRecord.reviewId());

        userRepo.findUserById(reviewRecord.userId()).orElseThrow(() -> new EntityNotFoundException(
                "Entity user not found", "User", "id", String.valueOf(reviewRecord.userId())));

        filmRepo.findFilmById(reviewRecord.filmId()).orElseThrow(() -> new EntityNotFoundException(
                "Entity Film not found", "Film", "Id", String.valueOf(reviewRecord.filmId())));

        ReviewDao reviewDao = ReviewMapper.updateFields(reviewRecord);

        reviewDao.setReviewId(reviewRecord.reviewId());

        ReviewDao updReviewDao = reviewRepo.putReview(reviewDao);

        feedService.postFeed(
                new FeedRecord(reviewRecord.userId(), updReviewDao.getReviewId(), EntityType.REVIEW, Operation.UPDATE)
        );

        return updReviewDao;
    }

    @Override
    public void deleteReview(Long id) {
        ReviewDao reviewById = getReviewById(id);

        reviewRepo.deleteReview(id);

        feedService.postFeed(
                new FeedRecord(reviewById.getUserId(), id, EntityType.REVIEW, Operation.REMOVE)
        );
    }

    @Override
    public ReviewDao getReviewById(Long id) throws EntityNotFoundException {
        return reviewRepo.getReviewById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Entity Review not found", "Review", "Id", String.valueOf(id)));
    }

    @Override
    public Collection<ReviewDao> getReviewByFilmId(Long filmId, Integer count) {
        filmRepo.findFilmById(filmId);

        return reviewRepo.getReviewByFilmId(filmId, count);
    }

    @Override
    public void reviewActions(Long id, Long userId, LikeOnReviewActions action) {
        getReviewById(id);
        userRepo.findUserById(userId);

        reviewRepo.reviewActions(id, userId, action);
    }
}

