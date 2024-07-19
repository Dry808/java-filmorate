package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.validation.CreateGroup;
import ru.yandex.practicum.filmorate.validation.UpdateGroup;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    // Получение списка всех режиссеров
    @GetMapping
    public List<Director> getAllDirectors() {
        log.info("Получение списка всех режиссеров");
        return directorService.getAllDirectors();
    }

    // Добавить режиссера
    @PostMapping
    public Director addUser(@Validated(CreateGroup.class) @RequestBody Director director) {
        log.info("Создаётся режиссер: {} ", director);
        directorService.addDirector(director);
        log.info("Директор успешно добавлен с идентификатором: {}", director.getId());
        return director;
    }

    // получить режиссера по ID
    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable int id) {
        log.info("Получение режиссерапо c ID + " + id);
        return directorService.getDirectorById(id);
    }

    // Удалить режиссера
    @DeleteMapping("/{id}")
    public void removeDirector(@PathVariable int id) {
        directorService.removeDirector(id);
        log.info("Режиссер с ID=" + id + " удален");
    }

    // Обновить режиссера
    @PutMapping
    public Director updateDirector(@Validated(UpdateGroup.class) @RequestBody Director director) {
        log.info("Обновляются данные режиссера c ID: {} ", director.getId());
        Director newDirector = directorService.updateDirector(director);
        log.info("Директор с ID: {} успешно обновлен", director.getId());
        return newDirector;
    }
}
