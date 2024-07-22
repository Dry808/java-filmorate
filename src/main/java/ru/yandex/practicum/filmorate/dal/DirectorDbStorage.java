package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
@Primary
public class DirectorDbStorage extends BaseRepository<Director> implements DirectorStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM directors";
    private static final String INSERT_QUERY = "INSERT INTO directors(director_name) VALUES (?)";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE director_id = ?";
    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM directors WHERE director_id = ?";
    private static final String UPDATE_QUERY = "UPDATE directors SET director_name = ?" +
            "WHERE director_id = ?";
    private static final String FIND_DIRECTOR_BY_FILM = "SELECT *\n" +
            "FROM DIRECTORS d \n" +
            "WHERE DIRECTOR_ID IN (SELECT DIRECTOR_ID \n" +
            "FROM FILM_DIRECTOR fd\n" +
            "WHERE FILM_ID = ?);";

    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    // Получение списка режиссеров
    @Override
    public List<Director> getAllDirectors() {
        return findMany(FIND_ALL_QUERY);
    }

    // Получение режиссера по id
    @Override
    public Director getDirectorById(int id) {
        Optional<Director> directorOptional = findOne(FIND_BY_ID_QUERY, id);
        return directorOptional.orElseThrow(() -> new NotFoundException("Режиссера с ID=" + id + " не найден"));
    }

    // Добавление режиссера в БД
    @Override
    public Director addDirector(Director director) {
        int id = insert(INSERT_QUERY, director.getName());
        director.setId(id);
        return director;
    }

    // Обновление режиссера в БД
    @Override
    public Director updateDirector(Director newDirector) {
        update(UPDATE_QUERY, newDirector.getName(), newDirector.getId());
        return newDirector;
    }

    // Удаление режиссера из БД
    @Override
    public void removeDirector(int directorId) {
        delete(DELETE_DIRECTOR_QUERY, directorId);
    }

    //Получение режиссеров по ID фильма
    @Override
    public Set<Director> getDirectorsFromFilm(int filmId) {
        List<Director> directorList = jdbc.query(FIND_DIRECTOR_BY_FILM, (rs, row) ->
                new Director(rs.getInt("director_id"), rs.getString("director_name")), filmId);
        return new HashSet<>(directorList);
    }
}
