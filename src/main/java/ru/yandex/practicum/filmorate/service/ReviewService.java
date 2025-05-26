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

    @Override
    public ReviewDao addLikeReview(Long id, Long userId) {
        getReviewById(id);

        userRepo.findUserById(userId);

        reviewRepo.addLikeReview(id, userId);

        return getReviewById(id);
    }

    @Override
    public ReviewDao addDislikeReview(Long id, Long userId) {
        getReviewById(id);

        userRepo.findUserById(userId);

        reviewRepo.addDislikeReview(id, userId);

        return getReviewById(id);
    }

    @Override
    public ReviewDao deleteLikeReview(Long id, Long userId) {
        getReviewById(id);

        userRepo.findUserById(userId);

        reviewRepo.deleteLikeReview(id, userId);

        return getReviewById(id);
    }

    @Override
    public ReviewDao deleteDislikeReview(Long id, Long userId) {
        getReviewById(id);

        userRepo.findUserById(userId);

        reviewRepo.deleteDislikeReview(id, userId);

        return getReviewById(id);
    }
}

