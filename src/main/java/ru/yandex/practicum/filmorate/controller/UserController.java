package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.CreateGroup;
import ru.yandex.practicum.filmorate.validation.UpdateGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для взаимодействия с пользователем
 */

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private Map<Integer, User> users = new HashMap<>();

    // Добавить пользователя
    @PostMapping
    public User addUser(@Validated(CreateGroup.class) @RequestBody User user) {
        log.info("Создаётся пользователь: {} ", user);

        if (user.getName() == null) {  // Если имя пустое - нужно использовать логин
            user.setName(user.getLogin());
        }
        user.setId(getNextUserId()); // устанавливаем ID
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен с идентификатором: {}", user.getId());
        return user;
    }

    // Обновить существуюшего пользователя
    @PutMapping
    public User updateUser(@Validated(UpdateGroup.class) @RequestBody User user) {
        log.info("Обновляются данные пользователя c ID: {} ", user.getId());

        if (users.containsKey(user.getId())) {   // проверяем, существует ли пользователь с таким ID
            User oldUser = users.get(user.getId());
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

            return oldUser;
        } else {
            log.error("Попытка обновить данные пользователя с несуществующим ID: {}", user.getId());
            throw new ValidationException("Пользователя с ID=" + user.getId() + " не существует");
        }
    }

    // Получить список всех пользователей
    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    // Метод для создания ID пользователя
    private Integer getNextUserId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


}
