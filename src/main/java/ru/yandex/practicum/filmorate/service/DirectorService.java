package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class DirectorService {
    private final DirectorStorage directorStorage;

    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    // Добавление режиссера
    public Director addDirector(Director director) {
        return directorStorage.addDirector(director);
    }

    // Получение режиссера по id
    public Director getDirectorById(int id) {
        return directorStorage.getDirectorById(id);
    }
    // Удаление режиссера
    public void removeDirector(int directorId) {
        Director director = directorStorage.getDirectorById(directorId);
        directorStorage.removeDirector(directorId);
    }

    // Обновление режиссера
    public Director updateDirector(Director newDirector) {
        Director oldDirector = directorStorage.getDirectorById(newDirector.getId());

        if (oldDirector == null) {
            log.error("Попытка обновить несуществующего режиссера с ID: {}", newDirector.getId());
            throw new NotFoundException("Режиссера с ID= " + newDirector.getId() + " не существует");
        }

        if (newDirector.getName() != null && !newDirector.getName().trim().isEmpty()) {
            oldDirector.setName(newDirector.getName());
        }
        directorStorage.updateDirector(oldDirector);
        return oldDirector;
    }

    // Получение режиссеров фильма
    public Set<Director> getDirectorsFromFilm(int filmId) {
        return directorStorage.getDirectorsFromFilm(filmId);
    }

}
