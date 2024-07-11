package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    // получить список всех рейтингов MPA
    @GetMapping
    public List<Mpa> getAllMpa() {
        log.info("Получение списка всех рейтингов(MPA)");
        return mpaService.getAllMpa();
    }

    // получить Рейтинг MPA по ID
    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        log.info("Получение рейтинга(MPA) с ID" + id);
        return mpaService.getMpaById(id);
    }
}
