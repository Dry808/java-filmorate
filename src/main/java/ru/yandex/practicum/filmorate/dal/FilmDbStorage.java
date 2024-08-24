package ru.yandex.practicum.filmorate.dal;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.DirectorService;
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
    private static final String SORTING_FILMS_BY_LIKES = "SELECT f.id, f.name, f.description, f.release_date, " +
            "f.duration, f.rating_id, COUNT(fl.user_id) as like_count " +
            "FROM films f JOIN film_director fd ON f.id = fd.film_id " +
            "LEFT JOIN films_like fl ON f.id = fl.film_id " +
            "WHERE fd.director_id = ? " +
            "GROUP BY f.id " +
            "ORDER BY like_count DESC";
    private static final String FIND_COMMON_FILMS = "SELECT f.id FROM films f " +
            "JOIN films_like fl1 ON f.id = fl1.film_id AND fl1.user_id = ? " +
            "JOIN films_like fl2 ON f.id = fl2.film_id AND fl2.user_id = ? " +
            "GROUP BY f.id " +
            "ORDER BY COUNT(f.id) DESC";
    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String DELETE_ALL_LIKES_QUERY = "DELETE FROM films_like WHERE film_id = ?";
    private static final String FIND_FILMS_BY_TITLE = "SELECT * FROM films WHERE LOWER(name) " +
            "LIKE LOWER(CONCAT('%', ?, '%'))";
    private static final String FIND_FILMS_BY_DIRECTOR = "SELECT f.* FROM films f " +
            "JOIN film_director fd ON f.id = fd.film_id " +
            "JOIN directors d ON fd.director_id = d.director_id WHERE LOWER(d.director_name) LIKE LOWER(CONCAT('%', ?, '%'))";
    private static final String FIND_FILMS_BY_TITLE_AND_DIRECTOR = "SELECT f.* FROM films f WHERE LOWER(f.name) " +
            "LIKE LOWER(CONCAT('%', ?, '%')) OR EXISTS (SELECT 1 FROM film_director fd JOIN directors d ON fd.director_id = d.director_id " +
            "WHERE fd.film_id = f.id AND LOWER(d.director_name) LIKE LOWER(CONCAT('%', ?, '%'))) ORDER BY f.NAME DESC;";


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
        film.setDirectors(directorService.getDirectorsFromFilm(film.getId()));
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

    //Получение популярных фильмов
    @Override
    public List<Film> getMostPopularFilms(Integer count, Integer genreId, Integer year) {
        StringBuilder sql = new StringBuilder(
                "SELECT f.*, COUNT(fl.user_id) as likes " +
                        "FROM films f " +
                        "LEFT JOIN films_like fl ON f.id = fl.film_id " +
                        "LEFT JOIN film_genres fg ON f.id = fg.film_id "
        );

        List<Object> params = new ArrayList<>();
        boolean whereAdded = false;

        if (genreId != null) {
            sql.append("WHERE fg.genre_id = ? ");
            params.add(genreId);
            whereAdded = true;
        }

        if (year != null) {
            if (whereAdded) {
                sql.append("AND YEAR(f.release_date) = ? ");
            } else {
                sql.append("WHERE YEAR(f.release_date) = ? ");
            }
            params.add(year);
        }

        sql.append("GROUP BY f.id ")
                .append("ORDER BY likes DESC ");

        if (count != null) {
            sql.append("LIMIT ?");
            params.add(count);
        }

        List<Film> allFilms = jdbc.query(sql.toString(), params.toArray(), new FilmRowMapper());
        allFilms.forEach(film -> {
            film.setGenres(genreService.getGenresFromFilm(film.getId())); //установить жанр
            film.setLikes(getLikes(film.getId()));
            film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
            film.setDirectors(directorService.getDirectorsFromFilm(film.getId()));
        });

        return allFilms;
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


    public List<Film> searchFilm(String query, String by) {
        List<Film> filmList = new ArrayList<>();
        if (by.equals("title")) {
            log.info("Поиск по названию фильма: " + query);
            filmList = findMany(FIND_FILMS_BY_TITLE, query);
        }

        if (by.equals("director")) {
            log.info("Поиск по режиссёру: " + query);
            filmList = findMany(FIND_FILMS_BY_DIRECTOR, query);
        }

        if (by.equals("title,director")) {
            log.info("Поиск по режиссёру и названию фильма: " + query);
            filmList = findMany(FIND_FILMS_BY_TITLE_AND_DIRECTOR, query, query);
        }
        return filmList;
    }
}
