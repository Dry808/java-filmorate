package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.dal.mappers.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {UserDbStorage.class, UserRowMapper.class, UserService.class,
		FilmDbStorage.class, FilmRowMapper.class, GenreDbStorage.class, GenreRowMapper.class, GenreService.class,
		MpaDbStorage.class, MpaRowMapper.class, MpaService.class, DirectorService.class, DirectorDbStorage.class,
		DirectorRowMapper.class, ReviewDbStorage.class, ReviewRowMapper.class, ReviewService.class})
class FilmorateApplicationTests {
	private final UserDbStorage userStorage;
	private final FilmDbStorage filmStorage;
	private final GenreDbStorage genreStorage;
	private final MpaDbStorage mpaStorage;
	private final ReviewDbStorage reviewStorage;
	private User user;
	private User secondUser;
	private Film film;
	private Film secondFilm;
	private Review review;
	private Review secondReview;

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

		review = new Review(0, 1, 1, "It's a good movie", true, 0);

		secondReview = new Review(0, 2, 1, "Very bad!", false, 0);
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

	@Test
	public void testAddAndGetReview() {
		review.setFilmId(filmStorage.addFilm(film).getId());
		review.setUserId(userStorage.addUser(user).getId());
		reviewStorage.addReview(review);
		Review thatReview = reviewStorage.getReviewById(review.getReviewId());
		assertThat(review).hasFieldOrPropertyWithValue("reviewId", review.getReviewId());
		assertThat(thatReview).hasFieldOrPropertyWithValue("reviewId", review.getReviewId());
	}

	@Test
	public void testUpdateReview() {
		review.setFilmId(filmStorage.addFilm(film).getId());
		review.setUserId(userStorage.addUser(user).getId());
		reviewStorage.addReview(review);
		review.setContent("Actually this movie is bad 😡");
		review.setIsPositive(false);
		reviewStorage.updateReview(review);
		Review thatReview = reviewStorage.getReviewById(review.getReviewId());
		assertThat(thatReview).hasFieldOrPropertyWithValue("reviewId", review.getReviewId());
		assertThat(thatReview).hasFieldOrPropertyWithValue("content", review.getContent());
		assertThat(thatReview).hasFieldOrPropertyWithValue("isPositive", false);
	}

	@Test
	public void testGetAllReviews() {
		review.setFilmId(filmStorage.addFilm(film).getId());
		secondReview.setFilmId(filmStorage.addFilm(secondFilm).getId());
		review.setUserId(userStorage.addUser(user).getId());
		secondReview.setUserId(review.getUserId());
		reviewStorage.addReview(review);
		reviewStorage.addReview(secondReview);
		List<Review> reviews = reviewStorage.getAllReviews();
		assertThat(reviews).isEqualTo(List.of(review, secondReview));
	}

	@Test
	public void testDeleteReview() {
		review.setFilmId(filmStorage.addFilm(film).getId());
		secondReview.setFilmId(filmStorage.addFilm(secondFilm).getId());
		review.setUserId(userStorage.addUser(user).getId());
		secondReview.setUserId(review.getUserId());
		reviewStorage.addReview(review);
		reviewStorage.addReview(secondReview);
		List<Review> reviews = reviewStorage.getAllReviews();
		assertThat(reviews).isEqualTo(List.of(review, secondReview));
		reviewStorage.deleteReviewById(review.getReviewId());
		reviews = reviewStorage.getAllReviews();
		assertThat(reviews).isEqualTo(List.of(secondReview));
	}

	@Test
	public void testAddAndRemoveLikeReview() {
		review.setFilmId(filmStorage.addFilm(film).getId());
		review.setUserId(userStorage.addUser(user).getId());
		reviewStorage.addReview(review);
		Review thatReview = reviewStorage.getReviewById(review.getReviewId());
		assertThat(thatReview).hasFieldOrPropertyWithValue("useful", 0);
		reviewStorage.addLike(thatReview.getReviewId(), user.getId());
		thatReview = reviewStorage.getReviewById(review.getReviewId());
		assertThat(thatReview).hasFieldOrPropertyWithValue("useful", 1);
		reviewStorage.removeLike(thatReview.getReviewId(), user.getId());
		thatReview = reviewStorage.getReviewById(review.getReviewId());
		assertThat(thatReview).hasFieldOrPropertyWithValue("useful", 0);
	}

	@Test
	public void testAddAndRemoveDislikeReview() {
		review.setFilmId(filmStorage.addFilm(film).getId());
		review.setUserId(userStorage.addUser(user).getId());
		reviewStorage.addReview(review);
		Review thatReview = reviewStorage.getReviewById(review.getReviewId());
		assertThat(thatReview).hasFieldOrPropertyWithValue("useful", 0);
		reviewStorage.addDislike(thatReview.getReviewId(), user.getId());
		thatReview = reviewStorage.getReviewById(review.getReviewId());
		assertThat(thatReview).hasFieldOrPropertyWithValue("useful", -1);
		reviewStorage.removeDislike(thatReview.getReviewId(), user.getId());
		thatReview = reviewStorage.getReviewById(review.getReviewId());
		assertThat(thatReview).hasFieldOrPropertyWithValue("useful", 0);
	}
}
