package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validation.ModelValidator;
import ru.yandex.practicum.filmorate.validation.ValidationResult;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final DirectorDbStorage directorDbStorage;
    private final EventFeedService eventFeedService;
    private final UserService userService;


    public FilmService(FilmStorage filmStorage, DirectorDbStorage directorDbStorage, EventFeedService eventFeedService,
                       UserService userService) {
        this.filmStorage = filmStorage;
        this.directorDbStorage = directorDbStorage;
        this.eventFeedService = eventFeedService;
        this.userService = userService;
    }

    // Добавление фильма
    public Film addFilm(Film film) {
        ValidationResult validationResult = ModelValidator.validateFilm(film);
        if (!validationResult.isValid()) { // проверка
            throw new ValidationException(validationResult.getCurrentError());
        }

        if (film.getGenres() != null) {
            List<Genre> sortedGenres = new ArrayList<>(film.getGenres()); // сортировка для тестов постман
            sortedGenres.sort(Comparator.comparing(Genre::getId));
            film.setGenres(new LinkedHashSet<>(sortedGenres));
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

        if (newFilm.getDirectors() != null) {
            oldFilm.setDirectors(newFilm.getDirectors());
        }

        if (newFilm.getMpa() != null) {
            oldFilm.setMpa(newFilm.getMpa());
        }

        if (newFilm.getGenres() != null) {
            List<Genre> sortedGenres = new ArrayList<>(newFilm.getGenres()); // сортировка для тестов постман
            sortedGenres.sort(Comparator.comparing(Genre::getId));
            newFilm.setGenres(new LinkedHashSet<>(sortedGenres));
            oldFilm.setGenres(newFilm.getGenres());

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
        eventFeedService.createEventFeed(userId, EventTypes.LIKE, Operations.ADD, filmId); // Запись события в БД
    }

    // Удаление лайка с фильма
    public void removeLike(int filmId, int userId) {
        filmStorage.removeLike(filmId, userId);
        userService.getUserById(userId); // проверка существования пользователя
        eventFeedService.createEventFeed(userId, EventTypes.LIKE, Operations.REMOVE, filmId); // Запись события в БД
    }

    // Получение топ-фильмов по лайкам
    public List<Film> getTopFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    // Сотрировка фильмов режиссера
    public List<Film> sortingFilms(int directorId, String sortBy) {

        Director director = directorDbStorage.getDirectorById(directorId);
        if (director == null) {
            log.error("Режиссера с ID: {} не существует", directorId);
            throw new NotFoundException("Режиссера с ID = " + directorId + " не существует");
        }

        return filmStorage.sortingFilms(directorId, sortBy);
    }

    // Получение общих фильмов
    public List<Film> getCommonFilms(int userId, int filmId) {
        List<Integer> commonFilmsId = filmStorage.getCommonFilms(userId, filmId);
        return commonFilmsId.stream()
                .map(this::getFilmById) // инициализация всех полей film
                .collect(Collectors.toList());
    }

    public Film deleteFilmById(int filmId) {
        return filmStorage.deleteFilmById(filmId);
    }

    public List<Film> getMostPopularFilms(Integer count, Integer genreId, Integer year) {
        return filmStorage.getMostPopularFilms(count, genreId, year);
    }

    public List<Film> searchFilms(String query, String by) {
        List<Film> filmsList = filmStorage.searchFilm(query, by);
        return filmsList.stream()
                .map(film -> getFilmById(film.getId()))
                .peek(film -> film.setDirectors(directorDbStorage.getDirectorsFromFilm(film.getId())))
                .collect(Collectors.toList());

    }

}
