package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.CreateGroup;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * * Модель, описывающая пользователей приложения
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private Set<Integer> friends = new HashSet<>();


    @JsonIgnore
    public int getFriendsCount() {
        return friends.size();
    }

}
