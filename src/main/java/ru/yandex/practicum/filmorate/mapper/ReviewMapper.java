package ru.yandex.practicum.filmorate.mapper;

import lombok.NoArgsConstructor;

import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dto.ReviewRecord;

@NoArgsConstructor
public class ReviewMapper {

    public static ReviewDao toReviewDao(ReviewRecord req) {

        return ReviewDao.builder()
                .content(req.content())
                .isPositive(req.isPositive())
                .filmId(req.filmId())
                .userId(req.userId())
                .reviewId(req.reviewId())
                .build();


    }

    public static ReviewDao updateFields(ReviewRecord req) {
        ReviewDao review = new ReviewDao();

        if (req.content() != null) {
            review.setContent(req.content());
        }

        if (req.isPositive() != null) {
            review.setIsPositive(req.isPositive());
        }

        if (req.filmId() != null) {
            review.setFilmId(req.filmId());
        }

        if (req.userId() != null) {
            review.setUserId(req.userId());
        }

        if (req.useful() != null) {
            review.setUseful(req.useful());
        } else {
            review.setUseful(0);
        }

        return review;
    }
}
