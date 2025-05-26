package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.storage.interfaces.IReviewRepo;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component
public class ReviewRepo extends BaseRepo<ReviewDao> implements IReviewRepo{

    public ReviewRepo(JdbcTemplate jdbc, RowMapper<ReviewDao> mapper) {
        super(jdbc, mapper);
    }

    private static final String INSERT_QUERY = "INSERT INTO review (content, is_positive, user_id, film_id, useful)\n" +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_QUERY = "SELECT * FROM review WHERE review_id = ?";

    private static final String DELETE_QUERY = "DELETE FROM review WHERE review_id = ?";

    private static final String UPDATE_QUERY = "UPDATE review SET content = ?, is_positive = ?," +
            "user_id = ?, film_id = ? WHERE review_id = ?";

    private static final String SELECT_REVIEW_BY_FILM_ID_QUERY = "SELECT * FROM review WHERE film_id = ? " +
            "ORDER BY useful DESC LIMIT ?";

    private static final String INSERT_LIKE_QUERY = "INSERT INTO liked_reviews VALUES ( ?, ?, 1 )";

    private static final String INSERT_DISLIKE_QUERY = "INSERT INTO liked_reviews VALUES ( ?, ?, -1 )";

    private static final String DELETE_LIKE_QUERY = "DELETE FROM liked_reviews WHERE review_id = ? AND user_id = ? AND estimation = 1";

    private static final String DELETE_DISLIKE_QUERY = "DELETE FROM liked_reviews WHERE review_id = ? AND user_id = ? AND estimation = -1";

    private static final String UPDATE_USEFUL_QUERY = "UPDATE review SET useful = ( select SUM(estimation) " +
            "FROM liked_reviews WHERE review_id = ?) WHERE review_id = ?";

    @Override
    public ReviewDao postReview(ReviewDao review) {
        log.info("Posting review {}", review);

        long id = insert(
                INSERT_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                0
        );
        review.setReviewId(id);

        return review;
    }

    @Override
    public Optional<ReviewDao> getReviewById(Long id) {
        log.trace("ReviewRepo.getReviewById: by id {}", id);

        return findOne(SELECT_QUERY, id);
    }

    @Override
    public void deleteReview(Long id) {
        log.trace("ReviewRepo.deleteReview: by id {}", id);

        delete(DELETE_QUERY, id);
    }

    @Override
    public ReviewDao putReview(ReviewDao review) {
        log.trace("ReviewRepo.putReview: by id {}", review);

        update(
                UPDATE_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getReviewId()
        );

        return review;
    }

    @Override
    public Collection<ReviewDao> getReviewByFilmId(Long id, Integer count) {
        log.trace("ReviewRepo.getReviewByFilmId: by id {}", id);

        return findMany(SELECT_REVIEW_BY_FILM_ID_QUERY, id, count);
    }

    @Override
    public void addLikeReview(Long id, Long userId) {
        log.trace("ReviewRepo.addLikeReview: by id {}, userId {}", id, userId);

        update(
                INSERT_LIKE_QUERY, id, userId
        );

        update(
                UPDATE_USEFUL_QUERY, id, id
        );

    }

    @Override
    public void addDislikeReview(Long id, Long userId) {
        log.trace("ReviewRepo.addDislikeReview: by id {}, userId {}", id, userId);

        update(
                INSERT_DISLIKE_QUERY, id, userId
        );

        update(
                UPDATE_USEFUL_QUERY, id, id
        );

    }

    @Override
    public void deleteLikeReview(Long id, Long userId) {
        log.trace("ReviewRepo.deleteLikeReview: by id {}, userId {}", id, userId);

        delete(
                DELETE_LIKE_QUERY, id, userId
        );

        update(
                UPDATE_USEFUL_QUERY, id, id
        );

    }

    @Override
    public void deleteDislikeReview(Long id, Long userId) {
        log.trace("ReviewRepo.deleteDislikeReview: by id {}, userId {}", id, userId);

        delete(
                DELETE_DISLIKE_QUERY, id, userId
        );

        update(
                UPDATE_USEFUL_QUERY, id, id
        );

    }
}

