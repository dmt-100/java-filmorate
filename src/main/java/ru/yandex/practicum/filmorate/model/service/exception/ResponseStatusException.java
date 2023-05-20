package ru.yandex.practicum.filmorate.model.service.exception;

public class ResponseStatusException extends RuntimeException {
    public ResponseStatusException(String s) {
        super(s);
    }
}
