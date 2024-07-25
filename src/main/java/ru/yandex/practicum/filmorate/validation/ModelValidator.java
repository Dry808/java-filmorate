package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

/**
 * Класс для проверки значений полей объектов User и Film
 */

@Slf4j
public class ModelValidator {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private static final int DESCRIPTION_MAX_SIZE = 200;


    public static ValidationResult validateFilm(Film film) {
        // проверка, что название фильма не может быть пустым
        if (film.getName() == null || film.getName().trim().isEmpty()) {
            String currentError = "Название фильма не может быть пустым";
            log.error(currentError);
            return new ValidationResult(false, currentError);
        }

        // проверка, что размер описания не может быть больше установленного размера
        if (film.getDescription().length() > DESCRIPTION_MAX_SIZE) {
            String currentError = "Описание не может быть больше " + DESCRIPTION_MAX_SIZE + " символов";
            log.error(currentError);
            return new ValidationResult(false, currentError);
        }

        // проверка, что дата релиза фильма не можеть быть раньше даты появляния кино
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            String currentError = "Дата релиза фильма не может быть раньше: " + CINEMA_BIRTHDAY;
            log.error(currentError);
            return new ValidationResult(false, currentError);
        }

        // проверка, что продолжительность не может быть отрицательной
        if (film.getDuration() < 0) {
            String currentError = "Продолжительность фильма не может быть отрицательной";
            log.error(currentError);
            return new ValidationResult(false, currentError);
        }
        return new ValidationResult(true);
    }


    public static ValidationResult validateUser(User user) {
        // Проверка электронной почты
        if (user.getEmail() == null || user.getEmail().trim().isEmpty() || !user.getEmail().contains("@")) {
            String currentError = "Неправильно указана электронная почта";
            log.error(currentError);
            return new ValidationResult(false, currentError);
        }

        // Проверка, что логин не может быть пустым или содержать пробелы
        if (user.getLogin() == null || user.getLogin().trim().isEmpty() || user.getLogin().contains(" ")) {
            String currentError = "Неправильно указан логин";
            log.error(currentError);
            return new ValidationResult(false, currentError);
        }

        // Проверка, что дата рождения не может быть в будущем
        if (user.getBirthday().isAfter(LocalDate.now())) {
            String currentError = "Дата рождения не может быть в будущем";
            log.error(currentError);
            return new ValidationResult(false, currentError);
        }
        return new ValidationResult(true);
    }

    public static ValidationResult validateReview(Review review) {
        // Проверка содержимого отзыва
        if (review.getContent() == null) {
            String currentError = "Пустое содержимое";
            log.error(currentError);
            return new ValidationResult(false, currentError);
        }

        // Проверка наличия типа отзыва
        if (review.getIsPositive() == null) {
            String currentError = "Не указан тип отзыва";
            log.error(currentError);
            return new ValidationResult(false, currentError);
        }

        // Проверка наличия пользователя в отзыве
        if (review.getUserId() == 0) {
            String currentError = "Не указан пользователь";
            log.error(currentError);
            return new ValidationResult(false, currentError);
        }

        // Проверка наличия фильма в отзыве
        if (review.getFilmId() == 0) {
            String currentError = "Не указан фильм";
            log.error(currentError);
            return new ValidationResult(false, currentError);
        }

        return new ValidationResult(true);
    }
}
