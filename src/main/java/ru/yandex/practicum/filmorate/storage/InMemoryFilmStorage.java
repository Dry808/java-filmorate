package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
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
        return films.get(id);
    }

    @Override
    public void addLike(int filmId, int userId) {
        getFilmById(filmId).getLikes().add(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        getFilmById(filmId).getLikes().remove(userId);
    }

    // метод для генерации ID фильма
    private int getNextFilmId() {
        return ++filmId;
    }


}
