package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.validation.CreateGroup;
import ru.yandex.practicum.filmorate.validation.UpdateGroup;
import java.util.List;


/**
 * Контроллер для взаимодействия с отзывами на фильмамы
 */
@Slf4j
@RestController()
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    // Добавление отзыва
    @PostMapping
    public Review addReview(@Validated(CreateGroup.class) @RequestBody Review review) {
        log.info("Добавляем новый отзыв: {}", review);
        review = reviewService.addReview(review);
        log.info("Отзыв успешно добавлен с ID: {}", review.getReviewId());
        return review;
    }

    // Обновление существующего отзыва
    @PutMapping
    public Review updateReview(@Validated(UpdateGroup.class) @RequestBody Review newReview) {
        log.info("Обновление отзыва с ID: {}", newReview.getReviewId());
        Review updatedReview = reviewService.updateReview(newReview);
        log.info("Отзыв с ID: {} успешно обновлен", newReview.getReviewId());
        return updatedReview;
    }

    // Получение отзыва по id
    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable int id) {
        log.info("Получение отзыва с ID=" + id);
        return reviewService.getReviewById(id);
    }

    // Удаление отзыва по id
    @DeleteMapping("/{id}")
    public void deleteReviewById(@PathVariable int id) {
        reviewService.deleteReviewById(id);
        log.info("Удалён отзыв с ID=" + id);
    }

    // Получение отзывов по идентификатору фильма
    @GetMapping()
    public List<Review> getReviews(@RequestParam Integer filmId, @RequestParam(defaultValue = "10") Integer count) {
        if (filmId == null) {
            log.info("Получение всех отзывов");
        } else {
            log.info("Получение " + count + " отзывов к фильму " + filmId);
        }
        return reviewService.getReviews(filmId, count);
    }

    // Добавление лайка
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        reviewService.addLike(id,userId);
        log.info("Отзыв с Id=" + id + " лайкнул пользователь с id=" + userId);
    }

    // Добавление дизлайка
    @PutMapping("/{id}/dislike/{userId}")
    public void addDisike(@PathVariable int id, @PathVariable int userId) {
        reviewService.addDislike(id,userId);
        log.info("Отзыв с Id=" + id + " дизлайкнул пользователь с id=" + userId);
    }

    // Удаление лайка
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        reviewService.removeLike(id,userId);
        log.info("Пользователь с id=" + userId + " убрал лайк с отзыва с id=" + id);
    }

    // Удаление дизлайка
    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable int id, @PathVariable int userId) {
        reviewService.removeDislike(id,userId);
        log.info("Пользователь с id=" + userId + " убрал дизлайк с отзыва с id=" + id);
    }
}
