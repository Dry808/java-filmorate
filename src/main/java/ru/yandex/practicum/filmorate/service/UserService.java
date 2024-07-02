package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.ModelValidator;
import ru.yandex.practicum.filmorate.validation.ValidationResult;


import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component
public class UserService {
    private final UserStorage inMemoryUserStorage;

    public UserService(UserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public User addUser(User user) {
        ValidationResult validationResult = ModelValidator.validateUser(user);
        if (!validationResult.isValid()) { // проверка
            throw new ValidationException(validationResult.getCurrentError());
        }

        if (user.getName() == null || user.getName().trim().isEmpty()) {  // Если имя пустое - нужно использовать логин
            user.setName(user.getLogin());
        }

        return inMemoryUserStorage.addUser(user);
    }

    public User updateUser(User user) {
        User oldUser = inMemoryUserStorage.getUserById(user.getId());
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

        return inMemoryUserStorage.updateUser(oldUser);
    }

    public List<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }

    public User getUserById(int id) {
        return inMemoryUserStorage.getUserById(id);
    }

    public void addFriend(int userId, int friendId) {
        inMemoryUserStorage.getUserById(userId).getFriends().add(friendId); // добавляем friendId в друзья userId
        inMemoryUserStorage.getUserById(friendId).getFriends().add(userId); // добавляем userId, в друзья friendId
    }

    public void removeFriend(int userId, int friendId) {
        inMemoryUserStorage.getUserById(userId).getFriends().remove(friendId);
        inMemoryUserStorage.getUserById(friendId).getFriends().remove(userId);
    }

    public List<User> getFriends(int id) {
        return getUserById(id).getFriends().stream()
                .map(inMemoryUserStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int friendId) {
        List<User> user1 = getFriends(userId);
        List<User> user2 = getFriends(friendId);
        return user1.stream()
                .filter(user2::contains)
                .collect(Collectors.toList());
    }
}
