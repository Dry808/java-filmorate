package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.ModelValidator;
import ru.yandex.practicum.filmorate.validation.ValidationResult;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component
public class UserService {
    private static final String STATUS_UNCONFIRMED = "unconfirmed";
    private static final String STATUS_CONFIRMED = "confirmed";
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public UserService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public User addUser(User user) {
        ValidationResult validationResult = ModelValidator.validateUser(user);
        if (!validationResult.isValid()) { // проверка
            throw new ValidationException(validationResult.getCurrentError());
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {  // Если имя пустое - нужно использовать логин
            user.setName(user.getLogin());
        }

        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        User oldUser = userStorage.getUserById(user.getId());
        if (oldUser == null) { // Проверка наличия пользователя с таким ID
            log.error("Попытка обновить данные пользователя с несуществующим ID: {}", user.getId());
            throw new NotFoundException("Пользователя с ID=" + user.getId() + " не существует");
        }

        if (user.getEmail() != null && user.getEmail().contains("@")) {
            oldUser.setEmail(user.getEmail());
        }

        if (user.getLogin() != null) {
            oldUser.setLogin(user.getLogin());
        }

        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }

        if (user.getBirthday() != null) {
            oldUser.setBirthday(user.getBirthday());
        }

        return userStorage.updateUser(oldUser);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public void addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        String status = STATUS_UNCONFIRMED;
        if (friend.getFriends().contains(userId)) {
            status = STATUS_CONFIRMED;
            userStorage.updateFriendStatus(friendId, userId, status);

        }
        userStorage.addFriend(userId, friendId, status);
    }

    public void removeFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        userStorage.removeFriend(userId, friendId);
        if (friend.getFriends().contains(userId)) {
            userStorage.updateFriendStatus(friendId, userId, STATUS_UNCONFIRMED);
        }
    }

    public List<User> getFriends(int userId) {
        getUserById(userId); // для проверки существования пользователя с таким ID
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int friendId) {
        List<User> user1 = getFriends(userId);
        List<User> user2 = getFriends(friendId);
        return user1.stream()
                .filter(user2::contains)
                .collect(Collectors.toList());
    }

    public User deleteUserById(int userId) {
        return userStorage.deleteUserById(userId);
    }

    public List<Film> getRecommendations(int userId) { //Рекомендации фильмов для пользователя
        Map<Integer, List<Film>> likedFilms = new HashMap<>();
        int maxOverlap = 0;
        List<Film> recommendations = new ArrayList<>();
        for (User u : userStorage.getAllUsers()) { //Получаем лайки всех пользователей
            likedFilms.put(u.getId(), new ArrayList<>());
            for (Film f : filmStorage.getAllFilms()) {
                if (f.getLikes().contains(u.getId())) {
                    likedFilms.get(u.getId()).add(f);
                }
            }
        }
        for (Integer u : likedFilms.keySet()) { //Ищем максимальное пересечение по лайкам
            if (u != userId) {
                int overlap = (int) likedFilms.get(u).stream().filter(x -> likedFilms.get(userId).contains(x)).count();
                if (overlap > maxOverlap) {
                    maxOverlap = overlap;
                    recommendations = likedFilms.get(u);
                }
            }
        }
        return recommendations.stream().filter(x -> !likedFilms.get(userId).contains(x)).collect(Collectors.toList());
    }
}
