package ru.yandex.practicum.filmorate.model.service.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String s) {
        super(s);
    }
}
