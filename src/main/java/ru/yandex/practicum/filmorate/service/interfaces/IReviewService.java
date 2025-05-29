package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dto.ReviewRecord;
import ru.yandex.practicum.filmorate.service.enums.LikeOnReviewActions;

import java.util.Collection;

public interface IReviewService {

    ReviewDao postReview(ReviewRecord reviewRecord);

    ReviewDao putReview(ReviewRecord reviewRecord);

    void deleteReview(Long id);

    ReviewDao getReviewById(Long id);

    Collection<ReviewDao> getReviewByFilmId(Long id, Integer count);

    void reviewActions(Long id, Long userId, LikeOnReviewActions action);
}
