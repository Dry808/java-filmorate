package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Set;

public interface DirectorStorage {
    List<Director> getAllDirectors();
    Director getDirectorById(int id);
    Director addDirector(Director director);
    Director updateDirector(Director newDirector);
    void removeDirector(int directorId);
    Set<Director> getDirectorsFromFilm(int filmId);




}
