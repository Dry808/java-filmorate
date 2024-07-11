package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Класс для взаимодействия Genre с БД
 */

@Slf4j
@Repository
@Primary
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genre";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";
    private static final String FIND_GENRE_BY_FILM = "SELECT film_genres.genre_id, genre.genre_name FROM film_genres " +
            "INNER JOIN genre ON film_genres.genre_id = genre.id WHERE film_genres.film_id = ?;";


    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc,mapper);
    }

    // Получение списка всех жанров из БД
    @Override
    public List<Genre> getAllGenres() {
        return findMany(FIND_ALL_QUERY);
    }

    // Получение жанра по ID
    @Override
    public Genre getGenreById(int id) {
        Optional<Genre> genreOptional = findOne(FIND_BY_ID_QUERY, id);
        return genreOptional.orElseThrow(() -> new NotFoundException("Жанр с ID=" + id + " не найден"));
    }

    //Получение жанра по ID фильма
    @Override
    public Set<Genre> getGenresFromFilm(int filmId) {
        List<Genre> genreList = jdbc.query(FIND_GENRE_BY_FILM, (rs, row) ->
                new Genre(rs.getInt("genre_id"), rs.getString("genre_name")), filmId);
        return new HashSet<>(genreList);
    }

}
