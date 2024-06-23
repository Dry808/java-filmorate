package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film newFilm);

    List<Film> getAllFilms();

    Film getFilmById(int id);

    void addLike(int FilmId, int userId);

    void removeLike(int filmId, int userId);

}
