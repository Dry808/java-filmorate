package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.ValidationException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
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
    FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    public FilmController() {}



    // Добавляем фильм
    @PostMapping
    public Film addFilm(@Validated(CreateGroup.class) @RequestBody Film film) {
        log.info("Добавляем новый фильм: {}", film);
        filmService.addFilm(film);
        log.info("Фильм успешно добавлен с ID: {}", film.getId());
        return film;
    }

    // Обновляем существующий фильм
    @PutMapping
    public Film updateFilm(@Validated(UpdateGroup.class) @RequestBody Film newFilm) {
        log.info("Обновление фильма с ID: {}", newFilm.getId());
        Film updatedFilm = filmService.updateFilm(newFilm);
        log.info("Фильм с ID: {} успешно обновлен", newFilm.getId());
        return updatedFilm;
    }

    // Получить список всех фильмов
    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }




}
