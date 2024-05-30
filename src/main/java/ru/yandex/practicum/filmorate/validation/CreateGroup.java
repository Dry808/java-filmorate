package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.groups.Default;

/**
 * Группа валидации при создании(POST-метод)
 * (наследуется от Default, чтобы не было необходимости явно указывать группу валидации)
 */
public interface CreateGroup extends Default {}
