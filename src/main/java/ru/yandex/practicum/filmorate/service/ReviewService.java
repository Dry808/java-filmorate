package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.ModelValidator;
import ru.yandex.practicum.filmorate.validation.ValidationResult;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public ReviewService(ReviewStorage reviewStorage, FilmStorage filmStorage, UserStorage userStorage) {
        this.reviewStorage = reviewStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    // Добавление отзыва
    public Review addReview(Review review) {
        ValidationResult validationResult = ModelValidator.validateReview(review);
        if (!validationResult.isValid()) { // проверка
            throw new ValidationException(validationResult.getCurrentError());
        }
        filmStorage.getFilmById(review.getFilmId());
        userStorage.getUserById(review.getUserId());
        return reviewStorage.addReview(review);
    }

    // Обновление отзыва
    public Review updateReview(Review newReview) {
        ValidationResult validationResult = ModelValidator.validateReview(newReview);
        if (!validationResult.isValid()) { // проверка
            throw new ValidationException(validationResult.getCurrentError());
        }
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
