package ru.yandex.practicum.filmorate.dal;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Primary
public class ReviewDbStorage extends BaseRepository<Review> implements ReviewStorage {
    private static final String INSERT_QUERY = "INSERT INTO reviews(film_id, user_id, content, is_positive)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE reviews SET film_id = ?, user_id = ?, content = ?," +
            "is_positive = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM reviews WHERE id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE id = ?";
    private static final String INSERT_QUERY_LIKE = "INSERT INTO review_likes(review_id, user_id, is_like) " +
            "VALUES (?, ?, ?)";
    private static final String DELETE_QUERY_LIKE = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
    private static final String GET_QUERY_LIKES = "SELECT * FROM review_likes WHERE review_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM reviews";

    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    // Добавление отзыва в БД
    @Override
    public Review addReview(Review review) {
        try {
            int id = insert(INSERT_QUERY,
                    review.getFilmId(),
                    review.getUserId(),
                    review.getContent(),
                    review.isPositive());
            review.setReviewId(id);
            return review;
        } catch (RuntimeException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    // Обновление отзыва в БД
    @Override
    public Review updateReview(Review newReview) {
        try {
            update(UPDATE_QUERY,
                    newReview.getFilmId(),
                    newReview.getUserId(),
                    newReview.getContent(),
                    newReview.isPositive());
            return newReview;
        } catch (RuntimeException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    // Получение отзыва из БД по id
    @Override
    public Review getReviewById(int id) {
        Optional<Review> optionalReview = findOne(FIND_BY_ID_QUERY, id);
        Review review = optionalReview.orElseThrow(() -> new NotFoundException("Отзыв с ID=" + id + " не найден"));
        review.setUseful(getLikes(review.getReviewId()));
        return review;
    }

    // Удаление отзыва из БД по id
    @Override
    public void deleteReviewById(int id) {
        delete(DELETE_QUERY, id);
    }

    // Получение отзывов из БД
    @Override
    public List<Review> getAllReviews() {
        List<Review> allReviews = findMany(FIND_ALL_QUERY);
        allReviews.forEach(r -> {r.setUseful(getLikes(r.getReviewId())); });
        return allReviews;
    }

    // Добавление лайка к отзыву
    @Override
    public void addLike(int id, int userId) {
        insertTwoKeys(INSERT_QUERY_LIKE, id, userId, true);
    }

    // Добавление дизлайка к отзыву
    @Override
    public void addDisike(int id, int userId) {
        insertTwoKeys(INSERT_QUERY_LIKE, id, userId, false);
    }

    // Удаление лайка к отзыву
    @Override
    public void removeLike(int id, int userId) {
        delete(DELETE_QUERY_LIKE, id, userId, true);
    }

    // Удаление дизлайка к отзыву
    @Override
    public void removeDislike(int id, int userId) {
        delete(DELETE_QUERY_LIKE, id, userId, false);
    }

    private int getLikes(int id) {
        List<Boolean> likes = jdbc.query(GET_QUERY_LIKES, (rs, rowNum) -> rs.getBoolean("is_like"), id);
        int useful = 0;
        for (Boolean l : likes) {
            if (l) {
                useful++;
            } else {
                useful--;
            }
        }
        return useful;
    }
}
