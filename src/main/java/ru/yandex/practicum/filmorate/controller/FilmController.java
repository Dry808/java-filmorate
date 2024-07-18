package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.CreateGroup;
import ru.yandex.practicum.filmorate.validation.UpdateGroup;

import java.util.List;


/**
 * Контроллер для взаимодействия с фильмами
 */

@Slf4j
@RestController()
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }


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
        log.info("Получение списка всех фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        log.info("Получение фильма с ID=" + id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
        log.info("Фильм с Id=" + id + " лайкнул пользователь с id=" + userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLike(id, userId);
        log.info("Пользователь с id=" + userId + " убрал лайк с фильма с id=" + id);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получение топ-" + count + " фильмов по лайкам");
        return filmService.getTopFilms(count);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        log.info("Получение списка общих фильмов пользователя с ID=" + userId + " и ID=" + friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

    //Удаление фильма по id
    @DeleteMapping("/{filmId}")
    public Film deleteFilm(@PathVariable int filmId) {
        log.info("Фильм удален id=" + filmId);
        return filmService.deleteFilmById(filmId);
    }
}
