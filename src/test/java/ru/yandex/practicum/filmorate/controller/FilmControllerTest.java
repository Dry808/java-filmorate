package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import ru.yandex.practicum.filmorate.model.Film;



import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    FilmController filmController;
    static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

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

        assertEquals(1, film.getId());
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

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void addFilmWithWrongDescriptionSize() {
        Film film = Film.builder()
                .name("Лодка")
                .description("В поисках спасения, человек вступает в странствие, " +
                        "окутанное загадочными приключениями и захватывающими историческими событиями. " +
                        "Он сталкивается с врагами, которые пытаются уничтожить его, и встречает новых союзников, " +
                        "которые помогают ему преодолевать трудности.")
                .releaseDate(LocalDate.of(1995, 12, 18))
                .duration(Duration.ofMinutes(90))
                .build();

        assertTrue(film.getDescription().length() > 200);
        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void addFilmWithWrongDateRelease() {
        Film film = Film.builder()
                .name("Человек-паук")
                .description("Супергерой спасает мир")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(Duration.ofMinutes(120))
                .build();

        assertTrue(film.getReleaseDate().isBefore(CINEMA_BIRTHDAY));
        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void addFilmWithWrongDuration() {
        Film film = Film.builder()
                .name("Человек-паук")
                .description("Супергерой спасает мир")
                .releaseDate(LocalDate.of(1995, 12, 29))
                .duration(Duration.ofMinutes(-120))
                .build();

        assertTrue(film.getDuration().isNegative());
        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }
}
