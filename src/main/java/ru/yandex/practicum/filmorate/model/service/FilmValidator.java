package ru.yandex.practicum.filmorate.model.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.repository.FilmRepository;

import java.time.LocalDate;

@Slf4j
public class FilmValidator {

    private int maxDescriptionLength = 200;

    public boolean validate(FilmRepository filmRepository, Film film) {
        if (film.getName().isBlank()) {
            log.info("Проверка поля name, film.getName().isBlank(): {}", film.getName().isBlank());
            throw new ValidationException("Название фильма не должно быть пустым, " + film.getName().isBlank());

        } else if (film.getDescription().length() > maxDescriptionLength) {
            log.info("Максимальное количество букв в описании фильма не должно превышать " +
                    maxDescriptionLength + ", film.getDescription().length: {}", film.getDescription().length());

            throw new ValidationException("Максимальное количество букв в описании фильма не должно превышать " +
                    maxDescriptionLength + ", film.getDescription(): " + film.getDescription());

        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Проверка на корректность даты фильма, film.getReleaseDate(): {}", film.getReleaseDate());
            throw new ValidationException("Некорректная дата фильма. Дата: " + film.getReleaseDate());

        } else if (film.getDuration() < 1) {
            log.info("Проверка на корректность продолжительности фильма, film.getDuration(): {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительной: " + film.getDuration());

        } else if (film.getId() > filmRepository.getFilms().size() + 1) {
            log.info("Проверка на корректность id фильма, film.getId(): {}", film.getId());
            throw new ValidationException("Некорректный id фильма. Id: " + film.getId());
        }
        return true;
    }
}
