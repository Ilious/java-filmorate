package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dto.ReviewRecord;

import java.util.Collection;

public interface IReviewService {

    ReviewDao postReview(ReviewRecord reviewRecord);

    ReviewDao putReview(ReviewRecord reviewRecord);

    void deleteReview(Long id);

    ReviewDao getReviewById(Long id);

    Collection<ReviewDao> getReviewByFilmId(Long id, Integer count);

    ReviewDao addLikeReview(Long id, Long userId);

    ReviewDao addDislikeReview(Long id, Long userId);

    ReviewDao deleteLikeReview(Long id, Long userId);

    ReviewDao deleteDislikeReview(Long id, Long userId);
}
