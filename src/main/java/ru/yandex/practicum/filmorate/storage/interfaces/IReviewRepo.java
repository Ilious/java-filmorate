package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.service.enums.LikeOnReviewActions;

import java.util.Collection;
import java.util.Optional;

public interface IReviewRepo {

    ReviewDao postReview(ReviewDao reviewRecord);

    Optional<ReviewDao> getReviewById(Long id);

    void deleteReview(Long id);

    ReviewDao putReview(ReviewDao reviewDao);

    Collection<ReviewDao> getReviewByFilmId(Long id, Integer count);

    void reviewActions(Long id, Long userId, LikeOnReviewActions action);

    Collection<ReviewDao> getAll();
}
