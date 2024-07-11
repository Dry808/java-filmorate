package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

/**
 * Класс для обработки исключений в приложении и возвращения соответствующих HTTP-ответов.
 */

@RestControllerAdvice
public class ErrorHandler {

    // Обрабатывает NotFoundException(при отсутствии ресурса) и возвращает HTTP код 404(not_found)
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse notFoundException(final NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    // Обрабатывает ValidationException(при ошибке валидации) и возвращает HTTP код 400
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse validationException(final ValidationException e) {
        return new ErrorResponse(e.getMessage());
    }


}

