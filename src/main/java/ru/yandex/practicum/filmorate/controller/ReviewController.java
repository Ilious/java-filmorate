package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dto.ReviewRecord;
import ru.yandex.practicum.filmorate.service.enums.LikeOnReviewActions;
import ru.yandex.practicum.filmorate.service.interfaces.IReviewService;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final IReviewService reviewService;

    public ReviewController(IReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDao createReview(
            @RequestBody @NotNull @Validated(Validator.OnCreate.class) ReviewRecord reviewRecord) {

        return reviewService.postReview(reviewRecord);
    }

    @PutMapping
    public ReviewDao updateReview(
            @RequestBody @NotNull @Validated(Validator.OnUpdate.class) ReviewRecord reviewRecord) {

        return reviewService.putReview(reviewRecord);
    }

    @GetMapping
    public Collection<ReviewDao> getAllReviews() {
        return reviewService.getAll();
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public ReviewDao getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping(params = "filmId")
    public Collection<ReviewDao> getReviewByFilmId(
            @RequestParam Long filmId, @RequestParam(required = false, defaultValue = "10")
            @Positive(message = "Count should be greater than 0") Integer count) {

        return reviewService.getReviewByFilmId(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.reviewActions(id, userId, LikeOnReviewActions.ADD_LIKE);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeReview(@PathVariable Long id, @PathVariable Long userId) {
       reviewService.reviewActions(id, userId, LikeOnReviewActions.ADD_DISLIKE);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLikeReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.reviewActions(id, userId, LikeOnReviewActions.DELETE_LIKE);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDislikeReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.reviewActions(id, userId, LikeOnReviewActions.DELETE_DISLIKE);
    }
}
