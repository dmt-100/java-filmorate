package ru.yandex.practicum.filmorate.model.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.repository.FilmRepository;

import java.time.LocalDate;

@Slf4j
public class FilmValidator {

    public boolean validate(FilmRepository filmRepository, Film film) {
        int maxDescriptionLength = 200;
        String name = film.getName();
        String description = film.getDescription();
        LocalDate releaseDate = film.getReleaseDate();
        int duration = film.getDuration();
        int id = film.getId();

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
            throw new ValidationException("Некорректная дата фильма. Дата: " + releaseDate);

        } else if (duration < 1) {
            log.info("Проверка на корректность продолжительности фильма, duration: {}", duration);
            throw new ValidationException("Продолжительность фильма должна быть положительной: " + duration);

        } else if (id > filmRepository.getFilms().size() + 1) {
            log.info("Проверка на корректность id фильма, id: {}", id);
            throw new ValidationException("Некорректный id фильма. Id: " + id);
        }
        return true;
    }
}
