package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.ModelValidator;
import ru.yandex.practicum.filmorate.validation.ValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

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
        User user = inMemoryUserStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователя с ID:" + id + " не существует");
        }

        return user;
    }

    public int addFriend(int userId, int friendId) {
        if (inMemoryUserStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователя с ID:" + userId + " не существует");
        }

        if (inMemoryUserStorage.getUserById(friendId) == null) {
            throw new NotFoundException("Пользователя с ID:" + friendId + " не существует");
        }

        return inMemoryUserStorage.addFriend(userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        if (inMemoryUserStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователя с ID:" + userId + " не существует");
        }

        if (inMemoryUserStorage.getUserById(friendId) == null) {
            throw new NotFoundException("Пользователя с ID:" + friendId + " не существует");
        }
        inMemoryUserStorage.removeFriend(userId, friendId);
    }

    public List<String> getFriends(int id) {
        return inMemoryUserStorage.getUserById(id).getFriends().stream()
                .map(inMemoryUserStorage::getUserById)
                .map(User::getName)
                .collect(Collectors.toList());
    }

    public List<String> getCommonFriends(int userId, int friendId) {
        Set<Integer> user1 = inMemoryUserStorage.getUserById(userId).getFriends();
        Set<Integer> user2 = inMemoryUserStorage.getUserById(friendId).getFriends();
        List<String> commonFriends = new ArrayList<>(); // список общих друзей

        for (Integer user : user1) {
            if (user2.contains(user)) {
                String friendName = inMemoryUserStorage.getUserById(user).getName();
                commonFriends.add(friendName);
            }
        }
        return commonFriends;
    }
}
