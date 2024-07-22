package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film newFilm);

    List<Film> getAllFilms();

    Film getFilmById(int id);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<Film> sortingFilms(int directorId, String sortBy);

    List<Integer> getCommonFilms(int userId, int friendId);

    Film deleteFilmById(int filmId);

    List<Film> getMostPopularFilms(Integer count, Integer genreId, Integer year);

    List<Film> searchFilm(String query, String by);
}
