package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {UserDbStorage.class, UserRowMapper.class, UserService.class,
		FilmDbStorage.class, FilmRowMapper.class, GenreDbStorage.class, GenreRowMapper.class, GenreService.class,
		MpaDbStorage.class, MpaRowMapper.class, MpaService.class})
class FilmorateApplicationTests {
	private final UserDbStorage userStorage;
	private final FilmDbStorage filmStorage;
	private final GenreDbStorage genreStorage;
	private final MpaDbStorage mpaStorage;
	private User user;
	private User secondUser;
	private Film film;
	private Film secondFilm;

	@BeforeEach
	public void beforeEach() {
		user = User.builder()
				.email("example@mail.ru")
				.login("ExampleLogin")
				.name("Джеки Чан")
				.birthday(LocalDate.of(1995, 12, 18))
				.build();

		secondUser = User.builder()
				.email("hogwarts@mail.ru")
				.login("Wizard")
				.name("Гарри Поттер")
				.birthday(LocalDate.of(1995, 12, 18))
				.build();

		film = Film.builder()
				.name("Человек-паук")
				.description("Фильм о парне в трико")
				.releaseDate(LocalDate.of(1995, 12, 18))
				.duration(90)
				.mpa(new Mpa(3, "PG-13"))
				.genres(Set.of(new Genre(4, "Триллер")))
				.build();

		secondFilm = Film.builder()
				.name("Бэтман")
				.description("Я - бэтман")
				.releaseDate(LocalDate.of(1995, 12, 18))
				.duration(190)
				.mpa(new Mpa(1, "G"))
				.genres(Set.of(new Genre(2, "Драма")))
				.build();
	}

	@Test
	public void testAddAndGetUser() {
		userStorage.addUser(user);
		User userTest = userStorage.getUserById(user.getId());

		assertThat(user).hasFieldOrPropertyWithValue("id", user.getId());
		assertThat(userTest).hasFieldOrPropertyWithValue("id", user.getId());
	}

	@Test
	public void testGetAllUsers() {
		userStorage.addUser(user);
		userStorage.addUser(secondUser);
		List<User> allUsers = userStorage.getAllUsers();

		assertThat(allUsers).isNotEmpty().contains(user, secondUser);
	}

	@Test
	public void testOperationWithFriends() {
		userStorage.addUser(user);
		userStorage.addUser(secondUser);

		userStorage.addFriend(user.getId(), secondUser.getId(), "unconfirmed");

		assertThat(userStorage.getFriends(user.getId())).contains(secondUser);
		assertThat(userStorage.getFriends(secondUser.getId())).isEmpty();

		userStorage.removeFriend(user.getId(),secondUser.getId());
		assertThat(userStorage.getFriends(user.getId())).isEmpty();
	}

	@Test
	public void testAddAndGetFilms() {
		filmStorage.addFilm(film);
		Film thatFilm = filmStorage.getFilmById(film.getId());
		assertThat(film).hasFieldOrPropertyWithValue("id", film.getId());
		assertThat(thatFilm).hasFieldOrPropertyWithValue("id", film.getId());
	}

	@Test
	public void testGetAllFilms() {
		filmStorage.addFilm(film);
		filmStorage.addFilm(secondFilm);
		List<Film> allFilms = filmStorage.getAllFilms();

		assertThat(allFilms).isNotEmpty().contains(filmStorage.getFilmById(film.getId()),
				filmStorage.getFilmById(secondFilm.getId()));
	}

	@Test
	public void testAddAndRemoveLike() {
		film = filmStorage.addFilm(film);
		user = userStorage.addUser(user);

		filmStorage.addLike(film.getId(),user.getId());
		assertThat(filmStorage.getFilmById(film.getId()).getLikes()).contains(user.getId());
		assertThat(filmStorage.getFilmById(film.getId()).getLikes()).hasSize(1);

		filmStorage.removeLike(film.getId(),user.getId());
		assertThat(filmStorage.getFilmById(film.getId()).getLikes()).isEmpty();
	}

	@Test
	public void testOperationWithGenres() {
		assertThat(genreStorage.getAllGenres()).isNotEmpty();
		assertThat(genreStorage.getGenreById(2)).hasFieldOrPropertyWithValue("name", "Драма");
	}

	@Test
	public void testMpaDbStorage() {
		assertThat(mpaStorage.getMpaById(1)).hasFieldOrPropertyWithValue("name", "G");
		assertThat(mpaStorage.getAllMpa()).isNotEmpty();
	}
}
