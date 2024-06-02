package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.ValidationException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.CreateGroup;
import ru.yandex.practicum.filmorate.validation.ModelValidator;
import ru.yandex.practicum.filmorate.validation.UpdateGroup;
import ru.yandex.practicum.filmorate.validation.ValidationResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для взаимодействия с фильмами
 */

@Slf4j
@RestController()
@RequestMapping("/films")
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private int filmId = 0;


    // Добавляем фильм
    @PostMapping
    public Film addFilm(@Validated(CreateGroup.class) @RequestBody Film film) {
        log.info("Добавляем новый фильм: {}", film);

        ValidationResult validationResult = ModelValidator.validateFilm(film);
        if (!validationResult.isValid()) { // проверка
            throw new ValidationException(validationResult.getCurrentError());
        }
        film.setId(getNextFilmId()); // установили id фильма
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен с ID: {}", film.getId());
        return film;
    }

    // Обновляем существующий фильм
    @PutMapping
    public Film updateFilm(@Validated(UpdateGroup.class) @RequestBody Film newFilm) {
        log.info("Обновление фильма с ID: {}", newFilm.getId());

        if (!films.containsKey(newFilm.getId())) {  // Проверка наличия фильма с таким ID
            log.error("Попытка обновить несуществующий фильм с ID: {}", newFilm.getId());
            throw new ValidationException("Фильма с ID= " + newFilm.getId() + " не существует");
        }
        Film oldFilm = films.get(newFilm.getId());
        if (newFilm.getName() != null && (newFilm.getName().trim().length() > 0)) {
            oldFilm.setName(newFilm.getName());
        }

        if (newFilm.getDescription() != null) {
            oldFilm.setDescription(newFilm.getDescription());
        }

        if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isAfter(CINEMA_BIRTHDAY)) {
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }

        if (newFilm.getDuration() != null) {
            oldFilm.setDuration(newFilm.getDuration());
        }
        log.info("Фильм с ID: {} успешно обновлен", newFilm.getId());
        return oldFilm;
    }

    // Получить список всех фильмов
    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    // метод для генерации ID фильма
    private int getNextFilmId() {
        return ++filmId;
    }


}
