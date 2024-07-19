package ru.yandex.practicum.filmorate.dal;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Slf4j
@Repository
@Primary
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, rating_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_QUERY_GENRE = "INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?);";
    private static final String INSERT_QUERY_DIRECTOR = "INSERT INTO film_director(film_id, director_id) VALUES (?, ?);";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?," +
            "duration = ?, rating_id = ? WHERE id = ?";
    private static final String DELETE_QUERY_GENRE = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String DELETE_QUERY_DIRECTOR = "DELETE FROM film_director WHERE film_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String FIND_USER_ID_FROM_LIKES = "SELECT user_id FROM films_like WHERE film_id = ?";
    private static final String INSERT_QUERY_LIKE = "INSERT INTO films_like(film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_QUERY_LIKE = "DELETE FROM films_like WHERE film_id = ? AND user_id = ?";
    private static final String SORTING_FILMS_BY_YEARS = "SELECT * FROM FILMS f WHERE ID IN" +
            " (SELECT FILM_ID FROM FILM_DIRECTOR fd WHERE fd.DIRECTOR_ID = ?) ORDER BY RELEASE_DATE;";
    private static final String SORTING_FILMS_BY_LIKES = "WITH film_count_like AS (SELECT f.*, COUNT(*) FROM FILMS f " +
            "LEFT JOIN FILMS_LIKE fl ON f.ID = fl.FILM_ID WHERE fl.FILM_ID IN (SELECT FILM_ID FROM FILM_DIRECTOR fd " +
            "WHERE DIRECTOR_ID = ?) GROUP BY f.ID ORDER BY COUNT(*) DESC) SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, " +
            "DURATION, RATING_ID FROM film_count_like";
    private static final String FIND_COMMON_FILMS = "SELECT f.id FROM films f " +
            "JOIN films_like fl1 ON f.id = fl1.film_id AND fl1.user_id = ? " +
            "JOIN films_like fl2 ON f.id = fl2.film_id AND fl2.user_id = ? " +
            "GROUP BY f.id " +
            "ORDER BY COUNT(f.id) DESC";
    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String DELETE_ALL_LIKES_QUERY = "DELETE FROM films_like WHERE film_id = ?";

    private MpaService mpaService;
    private GenreService genreService;
    private DirectorService directorService;


    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, MpaService mpaService, GenreService genreService, DirectorService directorService) {
        super(jdbc, mapper);
        this.mpaService = mpaService;
        this.genreService = genreService;
        this.directorService = directorService;
    }

    // Добавления фильма в БД
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
                for (Genre genre : film.getGenres()) {
                    insertTwoKeys(INSERT_QUERY_GENRE, id, genre.getId());
                }
            }
            //добавление режиссеров
            if (film.getDirectors() != null) {
                for (Director director : film.getDirectors()) {
                    insertTwoKeys(INSERT_QUERY_DIRECTOR, id, director.getId());
                }
            }

            return film;
        } catch (RuntimeException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    // Обновление фильма в БД
    @Override
    public Film updateFilm(Film film) {
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        // Удаление старых жанров и вставка новых если они есть
        if (film.getGenres() != null) {
            delete(DELETE_QUERY_GENRE, film.getId()); // Удаление старых жанров
            for (Genre genre : film.getGenres()) {
                insertTwoKeys(INSERT_QUERY_GENRE, film.getId(), genre.getId());
            }
        }
        // Удаление предыдущих режиссеров и вставка новых если они есть
        if (film.getDirectors() != null) {
            delete(DELETE_QUERY_DIRECTOR, film.getId()); // Удаление старого режиссера
            for (Director director : film.getDirectors()) {
                insertTwoKeys(INSERT_QUERY_DIRECTOR, film.getId(), director.getId());
            }
        }
        return film;
    }

    // Получение списка всех фильмов
    @Override
    public List<Film> getAllFilms() {
        List<Film> allFilms = findMany(FIND_ALL_QUERY);
        allFilms.forEach(film -> {
            film.setGenres(genreService.getGenresFromFilm(film.getId())); //установить жанр
            film.setLikes(getLikes(film.getId()));
            film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
            film.setDirectors(directorService.getDirectorsFromFilm(film.getId()));
        });
        return allFilms;
    }

    // Получение фильма по ID из БД
    @Override
    public Film getFilmById(int id) {
        Optional<Film> optionalFilm = findOne(FIND_BY_ID_QUERY, id);
        Film film = optionalFilm.orElseThrow(() -> new NotFoundException("Фильм с ID=" + id + " не найден"));
        film.setGenres(genreService.getGenresFromFilm(film.getId()));
        film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
        film.setLikes(getLikes(film.getId()));
        return film;
    }

    // Добавление лайков в БД
    @Override
    public void addLike(int filmId, int userId) {
        insertTwoKeys(INSERT_QUERY_LIKE, filmId, userId);
    }

    // Удаление лайков из БД
    @Override
    public void removeLike(int filmId, int userId) {
        delete(DELETE_QUERY_LIKE, filmId, userId);
    }

    // Получение ID общих фильмов
    @Override
    public List<Integer> getCommonFilms(int userId, int friendId) {
        List<Integer> filmIds = jdbc.query(FIND_COMMON_FILMS, (rs, rowNum) -> rs.getInt("id"), userId, friendId);
        return filmIds;
    }

    @Override
    public Film deleteFilmById(int filmId) {
        Film film = getFilmById(filmId);
        delete(DELETE_ALL_LIKES_QUERY, filmId);
        delete(DELETE_QUERY_GENRE, filmId);

        delete(DELETE_FILM_QUERY, filmId);
        return film;
    }

    // Получение лайков фильма
    private Set<Integer> getLikes(int filmId) {
        List<Integer> likes = jdbc.query(FIND_USER_ID_FROM_LIKES, (rs, rowNum) -> rs.getInt("user_id"), filmId);
        return new HashSet<>(likes);
    }

    // Получение списка отсортированных фильмов
    @Override
    public List<Film> sortingFilms(int directorId, String sortBy) {
        List<Film> films;

        switch (sortBy) {
            case "year" -> {
                films = findMany(SORTING_FILMS_BY_YEARS, directorId);
            }
            case "likes" -> {
                films = findMany(SORTING_FILMS_BY_LIKES, directorId);
            }
            default -> throw new NotFoundException("Сортировка по  " + sortBy + " недоступна");
        }
        films.forEach(film -> {
            film.setGenres(genreService.getGenresFromFilm(film.getId())); //установить жанр
            film.setLikes(getLikes(film.getId()));
            film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
            film.setDirectors(directorService.getDirectorsFromFilm(film.getId())); //установить режиссера
        });

        return films;
    }
}
