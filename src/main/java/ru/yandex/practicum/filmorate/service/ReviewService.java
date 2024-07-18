package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;

    public ReviewService (ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    // Добавление отзыва
    public Review addReview(Review review) {
        return reviewStorage.addReview(review);
    }

    // Обновление отзыва
    public Review updateReview(Review newReview) {
        return reviewStorage.updateReview(newReview);
    }

    // Получение отзыва
    public Review getReviewById(int id) {
        return reviewStorage.getReviewById(id);
    }

    // Удаление отзыва
    public void deleteReviewById(int id) {
        reviewStorage.deleteReviewById(id);
    }

    // Получение отзывов
    public List<Review> getReviews(Integer filmId, Integer count) {
        if (filmId == null) {
            return reviewStorage.getAllReviews().stream()
                    .sorted((x1, x2) -> x2.getUseful() - x1.getUseful())
                    .collect(Collectors.toList());
        } else {
            return reviewStorage.getAllReviews().stream()
                    .filter(x -> x.getFilmId() == filmId)
                    .sorted((x1, x2) -> x2.getUseful() - x1.getUseful())
                    .limit(count)
                    .collect(Collectors.toList());
        }
    }

    // Добавление лайка к отзыву
    public void addLike(int id, int userId) {
        reviewStorage.addLike(id, userId);
    }

    // Добавление дизлайка к отзыву
    public void addDislike(int id, int userId) {
        reviewStorage.addDislike(id, userId);
    }

    // Удаление лайка к отзыву
    public void removeLike(int id, int userId) {
        reviewStorage.removeLike(id, userId);
    }

    // Удаление дизлайка к отзыву
    public void removeDislike(int id, int userId) {
        reviewStorage.removeDislike(id, userId);
    }
}
