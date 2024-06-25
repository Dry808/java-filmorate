package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    User getUserById(int id);

    int addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

}
