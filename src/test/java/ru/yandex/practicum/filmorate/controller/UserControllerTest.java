package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


class UserControllerTest {
    UserController userController;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
    }

    @Test
    void addUserTest() {
        User user = User.builder()
                .email("example@mail.ru")
                .login("ExampleLogin")
                .name("Джеки Чан")
                .birthday(LocalDate.of(1995, 12, 18))
                .build();

        userController.addUser(user);

        assertEquals(1, user.getId());
        assertFalse(userController.getAllUsers().isEmpty());
    }

    @Test
    void addUserWithWrongEmail() {
        User user = User.builder()
                .email(" ") // не содержить почту
                .login("ExampleLogin")
                .name("Джеки Чан")
                .birthday(LocalDate.of(1995, 12, 18))
                .build();

        User user2 = User.builder()
                .email("pochta.ru") // неправильная почта
                .login("ExampleLogin")
                .name("Джеки Чан")
                .birthday(LocalDate.of(1995, 12, 18))
                .build();

        assertThrows(ValidationException.class, () -> userController.addUser(user));
        assertThrows(ValidationException.class, () -> userController.addUser(user2));
    }

    @Test
    void addUserWithWrongLogin() {
        User user = User.builder()
                .email("exemple@mail.ru")
                .login(" ") // пустой логин
                .name("Джеки Чан")
                .birthday(LocalDate.of(1993, 12, 18))
                .build();

        User user2 = User.builder()
                .email("true@pochta.ru")
                .login("Example Login") // содержит пробелы
                .name("Брюс Уиллис")
                .birthday(LocalDate.of(2003, 11, 18))
                .build();

        assertThrows(ValidationException.class, () -> userController.addUser(user));
        assertThrows(ValidationException.class, () -> userController.addUser(user2));
    }


    @Test
    void ifNameIsEmptyUseLogin() {
        User user = User.builder()
                .email("exemple@mail.ru")
                .login("Login")
                .name(" ") // пустое имя
                .birthday(LocalDate.of(1993, 12, 18))
                .build();

        userController.addUser(user);

        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void addUserWithWrongBirthday() {
        User user = User.builder()
                .email("exemple@mail.ru")
                .login("Login")
                .name("name") // пустое имя
                .birthday(LocalDate.now().plusYears(1)) // дата рождения в будущем
                .build();

        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }
}
