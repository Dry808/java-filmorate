package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> users = new HashMap<>();
    private int userId = 0;

    @Override
    public User addUser(User user) {
        user.setId(getNextUserId()); // устанавливаем ID
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        return users.put(user.getId(), user);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(int id) {
        if (users.get(id) == null) {
            throw new NotFoundException("Пользователя с ID:" + id + " не существует");
        }
        return users.get(id);
    }

    @Override
    @Deprecated
    public void addFriend(int userId, int friendId, String status) {
    }

    @Override
    @Deprecated
    public void removeFriend(int userId, int friendId) {

    }

    @Override
    @Deprecated
    public void updateFriendStatus(int userId, int friendId, String status) {
    }

    @Override
    @Deprecated
    public List<User> getFriends(int userId) {
        return List.of();
    }


    // Метод для создания ID пользователя
    private int getNextUserId() {
        return ++userId;
    }
}
