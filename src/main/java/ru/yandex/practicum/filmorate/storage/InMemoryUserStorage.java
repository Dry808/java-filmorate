package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

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
        return users.get(id);
    }

    @Override
    public int addFriend(int userId, int friendId) {
        users.get(userId).getFriends().add(friendId); // добавляем friendId в друзья userId
        users.get(friendId).getFriends().add(userId); // добавляем userId, в друзья friendId

        return users.get(userId).getFriends().size();
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        users.get(userId).getFriends().remove(friendId);
        users.get(friendId).getFriends().remove(userId);
    }


    // Метод для создания ID пользователя
    private int getNextUserId() {
        return ++userId;
    }
}
