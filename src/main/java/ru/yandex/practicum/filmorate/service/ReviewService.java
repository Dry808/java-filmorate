package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.EventTypes;
import ru.yandex.practicum.filmorate.model.Operations;
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
    private final EventFeedService eventFeedService;

    public ReviewService(ReviewStorage reviewStorage, FilmStorage filmStorage, UserStorage userStorage, EventFeedService eventFeedService) {
        this.reviewStorage = reviewStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.eventFeedService = eventFeedService;
    }

    // Добавление отзыва
    public Review addReview(Review review) {
        ValidationResult validationResult = ModelValidator.validateReview(review);
        if (!validationResult.isValid()) { // проверка
            throw new ValidationException(validationResult.getCurrentError());
        }
        filmStorage.getFilmById(review.getFilmId());
        userStorage.getUserById(review.getUserId());

        Review review1 = reviewStorage.addReview(review);
        eventFeedService.createEventFeed(review1.getUserId(), EventTypes.REVIEW, Operations.ADD, review1.getReviewId());
        return review1;                                                                         // Запись события в БД
    }

    // Обновление отзыва
    public Review updateReview(Review newReview) {
        ValidationResult validationResult = ModelValidator.validateReview(newReview);
        if (!validationResult.isValid()) { // проверка
            throw new ValidationException(validationResult.getCurrentError());
        }
        Review review = reviewStorage.updateReview(newReview);

        eventFeedService.createEventFeed(reviewStorage.getReviewById(review.getReviewId()).getUserId(), EventTypes.REVIEW, Operations.UPDATE,
                review.getReviewId()); // Запись события в БД
        return review;
    }

    // Получение отзыва
    public Review getReviewById(int id) {
        return reviewStorage.getReviewById(id);
    }

    // Удаление отзыва
    public void deleteReviewById(int id) {
        Review review = reviewStorage.getReviewById(id);
        reviewStorage.deleteReviewById(id);
        eventFeedService.createEventFeed(review.getUserId(), EventTypes.REVIEW, Operations.REMOVE, review.getReviewId());
    }                                                                                            // Запись события в БД

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
