package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.EventFeed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventFeedService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.CreateGroup;
import ru.yandex.practicum.filmorate.validation.UpdateGroup;

import java.util.List;

/**
 * Контроллер для взаимодействия с пользователем
 */

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final EventFeedService eventFeedService;

    public UserController(UserService userService, EventFeedService eventFeedService) {
        this.userService = userService;
        this.eventFeedService = eventFeedService;
    }

    // Добавить пользователя
    @PostMapping
    public User addUser(@Validated(CreateGroup.class) @RequestBody User user) {
        log.info("Создаётся пользователь: {} ", user);
        userService.addUser(user);
        log.info("Пользователь успешно добавлен с идентификатором: {}", user.getId());
        return user;
    }

    // Обновить существующего пользователя
    @PutMapping
    public User updateUser(@Validated(UpdateGroup.class) @RequestBody User user) {
        log.info("Обновляются данные пользователя c ID: {} ", user.getId());
        User newUser = userService.updateUser(user);
        log.info("Пользователь с ID: {} успешно обновлен", user.getId());
        return newUser;
    }

    // Получить список всех пользователей
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Получить пользователя по ID
    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    // Добавить в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
        log.info("Пользователь с ID=" + id + " добавил в друзья пользователя с ID=" + friendId);
    }

    // Удалить из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.removeFriend(id, friendId);
        log.info("Пользователь с ID=" + id + " удалил из друзей пользователя с ID=" + friendId);
    }

    // Получить список друзей
    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable int userId) {
        log.info("Получение списка друзей пользователя ID=" + userId);
        return userService.getFriends(userId);
    }

    // Получить список общих друзей
    @GetMapping("{id}/friends/common/{friendId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int friendId) {
        log.info("Получение списка общих друзей пользователей с ID=" + id + " и ID=" + friendId);
        return userService.getCommonFriends(id, friendId);
    }

    //Удаление юзера по id
    @DeleteMapping("/{userId}")
    public User deleteUser(@PathVariable int userId) {
        log.info("Пользователь удален id=" + userId);
        return userService.deleteUserById(userId);
    }

    //Просмотр последних событий на платформе
    @GetMapping("/{id}/feed")
    public List<EventFeed> viewRecentEvents(@PathVariable int id) {
        log.info("Получение последних событий для пользователя с id - " + id);
        return eventFeedService.viewRecentEvents(id);
    }
}
