package ru.yandex.practicum.filmorate.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(final String message) {
        super(message);
    }
}
