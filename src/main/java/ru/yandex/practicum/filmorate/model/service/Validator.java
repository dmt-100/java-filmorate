package ru.yandex.practicum.filmorate.model.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.exception.ValidationException;

import java.time.LocalDate;
import java.util.HashSet;

@Slf4j
@Component
public class Validator {

    public Validator() {
    }

    public static boolean validateFilm(Film film) {
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
            log.info("Максимальное количество букв в описании фильма не должно превышать {}," +
                    " description.length: {}", maxDescriptionLength, description.length());

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
        }
        return true;
    }

    public static boolean validateUser(User user) {
        final String login = user.getLogin();
        final String email = user.getEmail();
        final LocalDate birthDay = user.getBirthday();
        final String name = user.getName();
        final int id = user.getId();

        if (login.isBlank() || login.contains(" ")) {
            log.warn("Проверка поля login, login.isBlank(): {}, login.contains(\" \"): {}",
                    login.isBlank(), login.contains(" "));
            throw new ValidationException("У пользователя некорректный логин: " + login);

        } else if (email.isBlank() || !email.contains("@")) {
            log.warn("Проверка поля email, email.isBlank(): {}, email.contains(\"@\"): {}",
                    email.isBlank(), email.contains("@"));
            throw new ValidationException("У пользователя некорректный емейл: " + email);

        } else if (birthDay.isAfter(LocalDate.now())) {
            log.info("Проверка поля email, birthDay: {}", birthDay);
            throw new ValidationException("Некорректная дата рождения пользователя: " + birthDay);
        }
        if (user.isEmptyName()) {
            log.info("Проверка поля name на null, имя пользователя: {}", name);
            user.setName(user.getLogin());
            log.info("Обновленное имя пользователя: {}", user.getName());
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        return true;
    }

    public static void validateFilmId(int filmsSize, int id) {
        log.info("Проверка на корректность id фильма: {}", id);
        if (id < 0 || id > filmsSize + 1) {
            throw new ResourceNotFoundException("Некорректный id фильма: " + id);
        }
    }

    public static void validateUserId(int usersSize, int id) {
        log.info("Проверка на корректность id пользователя: {}", id);
        if (id < 0 || id > usersSize + 1) {
            throw new ResourceNotFoundException("Некорректный id пользователя: " + id);
        }
    }

}
