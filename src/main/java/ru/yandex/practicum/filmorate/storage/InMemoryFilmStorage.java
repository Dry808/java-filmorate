package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> films = new HashMap<>();
    private int filmId = 0;

    @Override
    public Film addFilm(Film film) {
        film.setId(getNextFilmId()); // установили id фильма
        films.put(film.getId(), film);

        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        return films.put(newFilm.getId(), newFilm);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(int id) {
        if (films.get(id) == null) {
            throw new NotFoundException("Фильма с ID=" + filmId + " не существует");
        }
        return films.get(id);
    }

    @Override
    @Deprecated
    public void addLike(int filmId, int userId) {
    }

    @Override
    @Deprecated
    public void removeLike(int filmId, int userId) {
    }

    @Override
    public List<Film> sortingFilms(int directorId, String sortBy) {
        return null;
    }

    @Deprecated
    public List<Integer> getCommonFilms(int userId, int friendId) {
        return List.of();
    }

    public Film deleteFilmById(int filmId) {
        return new Film();
    }

    @Override
    @Deprecated
    public List<Film> getMostPopularFilms(Integer count, Integer genreId, Integer year) {
        return new ArrayList<>();
    }

    @Override
    @Deprecated
    public List<Film> searchFilm(String query, String by) {
        return List.of();
    }


    // метод для генерации ID фильма
    private int getNextFilmId() {
        return ++filmId;
    }
}
