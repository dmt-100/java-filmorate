package ru.yandex.practicum.filmorate.model.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.service.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.service.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.storage.FilmInMemoryStorage;

import java.time.LocalDate;
import java.util.HashSet;

@Slf4j
public class FilmValidator {

    FilmInMemoryStorage storage;

    public FilmValidator(FilmInMemoryStorage storage) {
        this.storage = storage;
    }

    public boolean validate(Film film) {
        int maxDescriptionLength = 200;

        int id = film.getId();
        String name = film.getName();
        String description = film.getDescription();
        LocalDate releaseDate = film.getReleaseDate();
        int duration = film.getDuration();

        if (name.isBlank()) {
            log.info("Проверка поля name, name.isBlank(): {}", name.isBlank());
            throw new ValidationException("Название фильма не должно быть пустым, " + name.isBlank());

        } else if (description.length() > maxDescriptionLength) {
            log.info("Максимальное количество букв в описании фильма не должно превышать " +
                    maxDescriptionLength + ", description.length: {}", description.length());

            throw new ValidationException("Максимальное количество букв в описании фильма не должно превышать " +
                    maxDescriptionLength + ", description.length(): " + description.length());

        } else if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Проверка на корректность даты фильма, releaseDate: {}", releaseDate);
            throw new ValidationException("Некорректная дата фильма: " + releaseDate);

        } else if (duration < 1) {
            log.info("Проверка на корректность продолжительности фильма, duration: {}", duration);
            throw new ValidationException("Продолжительность фильма должна быть положительной: " + duration);

        } else if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());

        } else {
            log.info("Проверка на корректность id фильма: {}", id);
            validateId(id);
        }
        return true;
    }

    public void validateId(int id) {
        log.info("Проверка на корректность id фильма: {}", id);
        if (id < 0 || id > storage.getFilms().size() + 1) {
            throw new ResourceNotFoundException("Некорректный id фильма: " + id);
        }
    }

}
