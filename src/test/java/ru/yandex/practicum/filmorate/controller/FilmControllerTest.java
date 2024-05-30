package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.Film;



import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Проверяется поля описанные в п
 */

public class FilmControllerTest {
    FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
    }

    @Test
    void addFilmTest() {
        Film film = Film.builder()
                .name("Назад в будущее")
                .description("Фильм о путешествии во времени")
                .releaseDate(LocalDate.of(1995, 12, 18))
                .duration(Duration.ofMinutes(90))
                .build();

        filmController.addFilm(film);

        assertFalse(filmController.getAllFilms().isEmpty(), "Фильм не создан/не сохранён");
    }

    @Test
    void addFilmWithWrongName() {
        Film film = Film.builder()
                .name(" ")
                .description("Фильм о путешествии во времени")
                .releaseDate(LocalDate.of(1995, 12, 18))
                .duration(Duration.ofMinutes(90))
                .build();

        filmController.addFilm(film);

        assertThrows(MethodArgumentNotValidException.class, () -> filmController.addFilm(film));
    }
}
