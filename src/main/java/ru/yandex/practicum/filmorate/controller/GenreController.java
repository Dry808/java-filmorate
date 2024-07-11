package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

/**
 * Контроллер для взаимодействия с жанрами
 */

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    // получить список всех существующих жанров
    @GetMapping
    public List<Genre> getAllGenres() {
        log.info("Получение списка всех жанров");
        return genreService.getAllGenres();
    }

    // получить жанр по ID
    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) {
        log.info("Получение жанра c ID + " + id);
        return genreService.getGenreById(id);
    }


}
