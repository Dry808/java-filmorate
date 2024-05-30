package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

/**
 * Класс для проверки значений полей объектов User и Film
 */

@Slf4j
public class ModelValidator {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private static final int DESCRIPTION_MAX_SIZE = 200;
    public static String currentError;



    public static boolean validateFilm(Film film) {
        // проверка, что название фильма не может быть пустой
        if (film.getName() == null || film.getName().trim().isEmpty()) {
            currentError = "Название фильма не может быть пустым";
            log.error(currentError);
            return false;
        }

        // проверка, что размер описаине не может быть больше установленного размера
        if (film.getDescription().length() > DESCRIPTION_MAX_SIZE) {
            currentError = "Описание не может быть больше " + DESCRIPTION_MAX_SIZE + " символов";
            log.error(currentError);
            return false;
        }

        // проверка, что дата релиза фильма не можеть быть раньше даты появляния кино
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            currentError = "Дата релиза фильма не может быть раньше: " + CINEMA_BIRTHDAY;
            log.error(currentError);
            return false;
        }

        // проверка, что продолжительность не может быть отрицательной
        if (film.getDuration().isNegative()) {
            currentError = "Продолжительность фильма не может быть отрицательной";
            log.error(currentError);
            return false;
        }
        return true;
    }


    public static boolean validateUser(User user) {
        // Проверка электронной почты
        if (user.getEmail() == null || user.getEmail().trim().isEmpty() || !user.getEmail().contains("@")) {
            currentError = "Неправильно указана электронная почта";
            log.error(currentError);
            return false;
        }

        // Проверка, что логин не может быть пустым или содержать пробелы
        if (user.getLogin() == null || user.getLogin().trim().isEmpty() || user.getLogin().contains(" ")) {
            currentError = "Неправильно указан логин";
            log.error(currentError);
            return false;
        }

        // Проверка, что дата рождения не может быть в будущем
        if (user.getBirthday().isAfter(LocalDate.now())) {
            currentError = "Дата рождения не может быть в будущем";
            log.error(currentError);
            return false;
        }
        return true;
    }
}
