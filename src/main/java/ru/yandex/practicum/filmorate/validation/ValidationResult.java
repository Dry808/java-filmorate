package ru.yandex.practicum.filmorate.validation;

import lombok.Getter;

@Getter
public class ValidationResult {
    private boolean isValid;
    private String currentError;

    public ValidationResult(boolean isValid, String currentError) {
        this.isValid = isValid;
        this.currentError = currentError;
    }

    public ValidationResult(boolean isValid) {
        this.isValid = isValid;
    }



}
