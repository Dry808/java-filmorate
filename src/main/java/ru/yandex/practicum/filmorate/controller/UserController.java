package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.CreateGroup;
import ru.yandex.practicum.filmorate.validation.ModelValidator;
import ru.yandex.practicum.filmorate.validation.UpdateGroup;
import ru.yandex.practicum.filmorate.validation.ValidationResult;

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
    private int userId = 0;


    // Добавить пользователя
    @PostMapping
    public User addUser(@Validated(CreateGroup.class) @RequestBody User user) {
        log.info("Создаётся пользователь: {} ", user);

        ValidationResult validationResult = ModelValidator.validateUser(user);
        if (!validationResult.isValid()) { // проверка
            throw new ValidationException(validationResult.getCurrentError());
        }

        if (user.getName() == null || user.getName().trim().isEmpty()) {  // Если имя пустое - нужно использовать логин
            user.setName(user.getLogin());
        }
        user.setId(getNextUserId()); // устанавливаем ID
        users.put(user.getId(), user);

        log.info("Пользователь успешно добавлен с идентификатором: {}", user.getId());
        return user;
    }

    // Обновить существующего пользователя
    @PutMapping
    public User updateUser(@Validated(UpdateGroup.class) @RequestBody User user) {
        log.info("Обновляются данные пользователя c ID: {} ", user.getId());

        if (!users.containsKey(user.getId())) { // Проверка наличия пользователя с таким ID
            log.error("Попытка обновить данные пользователя с несуществующим ID: {}", user.getId());
            throw new ValidationException("Пользователя с ID=" + user.getId() + " не существует");
        }
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
        log.info("Пользователь с ID: {} успешно обновлен", user.getId());
        return oldUser;
    }

    // Получить список всех пользователей
    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    // Метод для создания ID пользователя
    private int getNextUserId() {
        return ++userId;
    }


}
