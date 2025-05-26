package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dto.ReviewRecord;

import java.util.Collection;
import java.util.Optional;

public interface IReviewRepo {

    ReviewDao postReview(ReviewDao reviewRecord);

    Optional<ReviewDao> getReviewById(Long id);

    void deleteReview(Long id);

    ReviewDao putReview(ReviewDao reviewDao);

    Collection<ReviewDao> getReviewByFilmId(Long id, Integer count);

    void addLikeReview(Long id, Long userId);

    void addDislikeReview(Long id, Long userId);

    void deleteLikeReview(Long id, Long userId);

    void deleteDislikeReview(Long id, Long userId);

}
