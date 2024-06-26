package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.CreateGroup;

import java.time.LocalDate;

/**
 * * Модель, описывающая пользователей приложения
 */

@Data
@Builder
public class User {
    private int id;
    @Email(message = "Неправильный формат email", groups = CreateGroup.class)
    private String email;
    @NotBlank(message = "Логин не может быть пустым", groups = CreateGroup.class)
    @Pattern(regexp = "^\\S+$", message = "Логин не может содержать пробелы")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

}
