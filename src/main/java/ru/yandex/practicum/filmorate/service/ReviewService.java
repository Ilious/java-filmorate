package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dto.ReviewRecord;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.service.interfaces.IReviewService;
import ru.yandex.practicum.filmorate.storage.FilmRepo;
import ru.yandex.practicum.filmorate.storage.ReviewRepo;
import ru.yandex.practicum.filmorate.storage.UserRepo;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {

    private final ReviewRepo reviewRepo;

    private final UserRepo userRepo;

    private final FilmRepo filmRepo;

    public enum LikeOnReviewActions {
        ADD_LIKE,
        ADD_DISLIKE,
        DELETE_LIKE,
        DELETE_DISLIKE
    }

    @Override
    public ReviewDao postReview(ReviewRecord reviewRecord) {
        userRepo.findUserById(reviewRecord.userId()).orElseThrow(() -> new EntityNotFoundException(
                "Entity user not found", "User", "id", String.valueOf(reviewRecord.userId())));

        filmRepo.findFilmById(reviewRecord.filmId()).orElseThrow(() -> new EntityNotFoundException(
                "Entity Film not found", "Film", "Id", String.valueOf(reviewRecord.filmId())));

        ReviewDao reviewDao = ReviewMapper.toReviewDao(reviewRecord);

        return reviewRepo.postReview(reviewDao);
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

        return reviewRepo.putReview(reviewDao);
    }

    @Override
    public void deleteReview(Long id) {
        reviewRepo.deleteReview(id);
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

    public void reviewActions(Long id, Long userId, LikeOnReviewActions action) {
        getReviewById(id);
        userRepo.findUserById(userId);

        switch (action) {
            case ADD_LIKE -> reviewRepo.addLikeReview(id, userId);
            case ADD_DISLIKE -> reviewRepo.addDislikeReview(id, userId);
            case DELETE_LIKE -> reviewRepo.deleteLikeReview(id, userId);
            case DELETE_DISLIKE -> reviewRepo.deleteDislikeReview(id, userId);
        }
    }

    @Override
    public void addLikeReview(Long id, Long userId) {
        reviewActions(id, userId, LikeOnReviewActions.ADD_LIKE);
    }

    @Override
    public void addDislikeReview(Long id, Long userId) {
        reviewActions(id, userId, LikeOnReviewActions.ADD_DISLIKE);
    }

    @Override
    public void deleteLikeReview(Long id, Long userId) {
        reviewActions(id, userId, LikeOnReviewActions.DELETE_LIKE);

    }

    @Override
    public void deleteDislikeReview(Long id, Long userId) {
        reviewActions(id, userId, LikeOnReviewActions.DELETE_DISLIKE);
    }
}

