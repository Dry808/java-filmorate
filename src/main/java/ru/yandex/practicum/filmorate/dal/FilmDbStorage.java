package ru.yandex.practicum.filmorate.dal;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Slf4j
@Repository
@Primary
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, rating_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_QUERY_GENRE = "INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?);";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?," +
            "duration = ?, rating_id = ? WHERE id = ?";
    private static final String DELETE_QUERY_GENRE = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String FIND_GENRE_FROM = "SELECT genre_id FROM film_genres WHERE film_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private String kek = "SELECT user_id FROM films_like WHERE film_id = ?";
    private static final String INSERT_QUERY_LIKE = "INSERT INTO films_like(film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_QUERY_LIKE = "DELETE FROM films_like WHERE film_id = ? AND user_id = ?";
    String query = "SELECT g.id, g.genre_name FROM genre g JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
    private static final String GET_TOP_FILMS = "SELECT f.*, COUNT(fl.user_id) AS likes_count FROM films f " +
            "LEFT JOIN films_like fl ON f.id = fl.film_id GROUP BY f.id ORDER BY likes_count DESC LIMIT ?";



    private MpaService mpaService;
    private GenreService genreService;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, MpaService mpaService, GenreService genreService) {
        super(jdbc, mapper);
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

    @Override
    public Film addFilm(Film film) {
        try {
            int id = insert(INSERT_QUERY,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId());
            film.setId(id);
            //добавление жанров
            if (film.getGenres() != null) {
                for(Genre genre : film.getGenres()) {
                    insertTwo(INSERT_QUERY_GENRE, id, genre.getId());
                }
            }

            return film;
        } catch (RuntimeException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @Override
    public Film updateFilm(Film film) {
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        // Удаление старых жанров и вставка новых
        if (film.getGenres() != null) {
            delete(DELETE_QUERY_GENRE, film.getId()); // Удаление старых жанров
            for (Genre genre : film.getGenres()) {
                insertTwo(INSERT_QUERY_GENRE, film.getId(), genre.getId());
            }
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> allFilms = findMany(FIND_ALL_QUERY);
        allFilms.forEach(film -> {
            film.setGenres(genreService.getGenresFromFilm(film.getId())); //установить жанр
            film.setLikes(getLikes(film.getId()));
          //
        });
        return allFilms;
    }


    @Override
    public Film getFilmById(int id) {
        Optional<Film> optionalFilm = findOne(FIND_BY_ID_QUERY, id);
        Film film =  optionalFilm.orElseThrow(() -> new NotFoundException("Фильм с ID=" + id + " не найден"));
        film.setGenres(genreService.getGenresFromFilm(film.getId()));
        film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
        film.setLikes(getLikes(film.getId()));
        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {
        insertTwo(INSERT_QUERY_LIKE, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        delete(DELETE_QUERY_LIKE, filmId, userId);
    }


    private Set<Integer> getLikes(int filmId) {
        List<Integer> likes = jdbc.query(kek,(rs, rowNum) -> rs.getInt("user_id"), filmId);
        return new HashSet<>(likes);
    }


}
