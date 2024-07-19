package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.CreateGroup;
import ru.yandex.practicum.filmorate.validation.UpdateGroup;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * * Модель, описывающая фильм
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    @NotNull(message = "ID фильма должен быть указан", groups = UpdateGroup.class)
    private Integer id;
    @NotBlank(message = "Название фильма не может быть пустым", groups = CreateGroup.class)
    private String name;
    @Size(max = 200, message = "Максимальная длина описания фильма — 200 символов")
    private String description;
    private LocalDate releaseDate;
    //@JsonSerialize(using = DurationSerializer.class)
    private Integer duration;
    private Set<Integer> likes = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
    private Mpa mpa;
    private Set<Director> directors = new HashSet<>();


    // Метод для подсчёта кол-ва лайков на фильме
    @JsonIgnore
    public Integer getLikesCount() {
        return likes.size();
    }
}
