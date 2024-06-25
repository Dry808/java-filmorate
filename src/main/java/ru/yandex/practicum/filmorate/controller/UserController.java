package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
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
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
    public int addFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.addFriend(id, friendId);
    }

    // Удалить из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.removeFriend(id, friendId);
    }

    // Получить список друзей
    @GetMapping("/{id}/friends")
    public List<String> getFriends(@PathVariable int id) {
        return userService.getFriends(id);
    }

    // Получить список общих друзей
    @GetMapping("{id}/friends/common/{friendId}")
    public List<String> getCommonFriends(@PathVariable int id, @PathVariable int friendId) {
        return userService.getCommonFriends(id, friendId);
    }






}
