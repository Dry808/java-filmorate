package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.groups.Default;

/**
 * Группа валидации при обновлении(PUT-метод)
 * (наследуется от Default, чтобы не было необходимости явно указывать группу валидации для всех полей)
 */
public interface UpdateGroup extends Default {
}
