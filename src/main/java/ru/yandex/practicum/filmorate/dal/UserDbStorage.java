package ru.yandex.practicum.filmorate.dal;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Primary
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_FRIEND_QUERY = "INSERT INTO user_friends(user_id, friend_id, friendship_status) " +
            "VALUES (?, ?, ?)";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
    private static final String UPDATE_FRIEND_STATUS = "UPDATE user_friends SET friendship_status = ? " +
            "WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_USER_FRIENDS_ID = "SELECT friend_id FROM user_friends WHERE user_id = ?";
    private static final String FIND_USER_FRIENDS =  "SELECT friend_id as id, email, login, name, birthday FROM user_friends " +
            "INNER JOIN users ON user_friends.friend_id = users.id WHERE user_friends.user_id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public User addUser(User user) {
        int id = insert(INSERT_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        user.setId(id);
        user.setFriends(getUserFriendsId(user.getId()));
        return user;
    }

    @Override
    public User updateUser(User user) {
        update(UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public User getUserById(int id) {
        Optional<User> userOptional = findOne(FIND_BY_ID_QUERY, id);
        return userOptional.orElseThrow(() -> new NotFoundException("Пользователь с ID=" + id + " не найден"));
    }

    @Override
    public void addFriend(int userId, int friendId, String status) {
        insertTwo(INSERT_FRIEND_QUERY, userId, friendId, status);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        delete(DELETE_FRIEND_QUERY, userId, friendId);
    }


    public void updateFriendStatus(int userId, int friendId, String status) {
        update(UPDATE_FRIEND_STATUS, status, userId, friendId);
    }

    public List<User> getFriends(int userId) {
        List<User> userFriends = findMany(FIND_USER_FRIENDS, userId);
        return userFriends;
    }


    public Set<Integer> getUserFriendsId(int userId) {
        List<User> userFriends = findMany(FIND_USER_FRIENDS_ID, userId);
        return userFriends.stream()
                .map(User::getId)
                .collect(Collectors.toSet());
    }
}
