package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import ru.yandex.practicum.filmorate.adapter.DurationSerializer;
import ru.yandex.practicum.filmorate.validation.CreateGroup;
import ru.yandex.practicum.filmorate.validation.UpdateGroup;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * * Модель, описывающая фильм
 */

@Data
@Builder
public class Film {
    @NotNull(message = "ID фильма должен быть указан", groups = UpdateGroup.class)
    private Integer id;
    @NotBlank(message = "Название фильма не может быть пустым", groups = CreateGroup.class)
    private String name;
    @Size(max = 200, message = "Максимальная длина описания фильма — 200 символов")
    private String description;
    private LocalDate releaseDate;
    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;
    private final Set<Integer> likes = new HashSet<>();


    // Метод для подсчёта кол-ва лайков на фильме
    @JsonIgnore
    public Integer getLikesCount() {
        return likes.size();
    }
}
