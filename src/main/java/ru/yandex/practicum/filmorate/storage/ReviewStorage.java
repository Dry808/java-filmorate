package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;
import java.util.List;

public interface ReviewStorage {
    Review addReview(Review review);

    Review updateReview(Review newReview);

    Review getReviewById(int id);

    void deleteReviewById(int id);

    List<Review> getAllReviews();

    void addLike(int id, int userId);

    void addDislike(int id, int userId);

    void removeLike(int id, int userId);

    void removeDislike(int id, int userId);
}
