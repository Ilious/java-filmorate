package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.service.enums.LikeOnReviewActions;
import ru.yandex.practicum.filmorate.storage.interfaces.IReviewRepo;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component
public class ReviewRepo extends BaseRepo<ReviewDao> implements IReviewRepo {

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

    private static final String GET_ALL_REVIEWS_QUERY = "SELECT * FROM review";

    private final RowMapper<ReviewDao> mapper;

    public ReviewRepo(JdbcTemplate jdbc, RowMapper<ReviewDao> mapper) {
        super(jdbc);
        this.mapper = mapper;
    }

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

        return findOne(SELECT_QUERY, mapper, id);
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

        return findMany(
                SELECT_REVIEW_BY_FILM_ID_QUERY, mapper,  id, count
        );
    }

    @Override
    public void reviewActions(Long id, Long userId, LikeOnReviewActions action) {
        log.trace("ReviewRepo.{}: by id {}, userId {}", action, id, userId);
        switch (action) {
            case ADD_LIKE -> {
                delete(DELETE_DISLIKE_QUERY, id, userId);
                update(INSERT_LIKE_QUERY, id, userId);
            }
            case ADD_DISLIKE -> {
                delete(DELETE_LIKE_QUERY, id, userId);
                update(INSERT_DISLIKE_QUERY, id, userId);
            }
            case DELETE_LIKE -> delete(DELETE_LIKE_QUERY, id, userId);
            case DELETE_DISLIKE -> delete(DELETE_DISLIKE_QUERY, id, userId);
        }

        update(UPDATE_USEFUL_QUERY, id, id);
    }

    @Override
    public Collection<ReviewDao> getAll() {
        return
                findMany(
                GET_ALL_REVIEWS_QUERY, mapper
        );
    }
}

