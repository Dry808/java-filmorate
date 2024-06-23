package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validation.ModelValidator;
import ru.yandex.practicum.filmorate.validation.ValidationResult;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage inMemoryFilmStorage;
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    public FilmService(FilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public Film addFilm(Film film) {
        ValidationResult validationResult = ModelValidator.validateFilm(film);
        if (!validationResult.isValid()) { // проверка
            throw new ValidationException(validationResult.getCurrentError());
        }
        return inMemoryFilmStorage.addFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        Film oldFilm = inMemoryFilmStorage.getFilmById(newFilm.getId());
        if (oldFilm == null) {
            log.error("Попытка обновить несуществующий фильм с ID: {}", newFilm.getId());
            throw new ValidationException("Фильма с ID= " + newFilm.getId() + " не существует");
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

        inMemoryFilmStorage.updateFilm(oldFilm);

        return oldFilm;
    }

    public List<Film> getAllFilms() {
        return inMemoryFilmStorage.getAllFilms();
    }

    public void addLike(int filmId, int userId) {
        if (inMemoryFilmStorage.getFilmById(filmId) == null) {
            throw new ValidationException("Фильма с ID=" + filmId + " не существует");
        }

        inMemoryFilmStorage.addLike(filmId, userId);
    }
}
