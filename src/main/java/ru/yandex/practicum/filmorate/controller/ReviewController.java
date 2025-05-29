package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ReviewDao> createReview(
            @RequestBody @NotNull @Validated(Validator.OnCreate.class) ReviewRecord reviewRecord) {

        return ResponseEntity.status(HttpStatus.OK).body(reviewService.postReview(reviewRecord));
    }

    @PutMapping
    public ResponseEntity<ReviewDao> updateReview(
            @RequestBody @NotNull @Validated(Validator.OnUpdate.class) ReviewRecord reviewRecord) {

        return ResponseEntity.status(HttpStatus.OK).body(reviewService.putReview(reviewRecord));
    }

    @GetMapping
    public ResponseEntity<Collection<ReviewDao>> getAllReviews() {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getAll());
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDao> getReviewById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getReviewById(id));
    }

    @GetMapping(params = "filmId")
    public ResponseEntity<Collection<ReviewDao>> getReviewByFilmId(
            @RequestParam Long filmId, @RequestParam(required = false, defaultValue = "10")
            @Positive(message = "Count should be greater than 0") Integer count) {

        return ResponseEntity.status(HttpStatus.OK).body(reviewService.getReviewByFilmId(filmId, count));
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}/like/{userId}")
    public void addLikeReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.reviewActions(id, userId, LikeOnReviewActions.ADD_LIKE);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeReview(@PathVariable Long id, @PathVariable Long userId) {
       reviewService.reviewActions(id, userId, LikeOnReviewActions.ADD_DISLIKE);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.reviewActions(id, userId, LikeOnReviewActions.DELETE_LIKE);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.reviewActions(id, userId, LikeOnReviewActions.DELETE_DISLIKE);
    }
}
