package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validation.ModelValidator;
import ru.yandex.practicum.filmorate.validation.ValidationResult;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;


    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    // Добавление фильма
    public Film addFilm(Film film) {
        ValidationResult validationResult = ModelValidator.validateFilm(film);
        if (!validationResult.isValid()) { // проверка
            throw new ValidationException(validationResult.getCurrentError());
        }
        return filmStorage.addFilm(film);
    }

    // Обновление(редактирование) фильма
    public Film updateFilm(Film newFilm) {
        Film oldFilm = filmStorage.getFilmById(newFilm.getId());
        if (oldFilm == null) {
            log.error("Попытка обновить несуществующий фильм с ID: {}", newFilm.getId());
            throw new NotFoundException("Фильма с ID= " + newFilm.getId() + " не существует");
        }

        if (newFilm.getName() != null && !newFilm.getName().trim().isEmpty()) {
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

        filmStorage.updateFilm(oldFilm);

        return oldFilm;
    }

    // Получение всех фильмов
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId);
    }

    // Добавление лайка фильму
    public void addLike(int filmId, int userId) {
        filmStorage.addLike(filmId, userId);
    }

    // Удаление лайка с фильма
    public void removeLike(int filmId, int userId) {
        filmStorage.removeLike(filmId,userId);
    }

    // Получение топ-фильмов по лайкам
    public List<Film> getTopFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

}
