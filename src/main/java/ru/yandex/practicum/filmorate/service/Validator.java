package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;

@Slf4j
@Component
public class Validator {

    public Validator() {
    }

    public void validateFilm(Film film) {

        String name = film.getName();
        String description = film.getDescription();
        LocalDate releaseDate = film.getReleaseDate();
        int duration = film.getDuration();

        if (name.isBlank()) {
            log.info("Проверка поля name, name.isBlank(): {}", name.isBlank());
            throw new ValidationException("Название фильма не должно быть пустым.");

        } else if (description.length() > 200) {
            log.info("Максимальное количество букв в описании фильма не должно превышать {}," +
                    " description.length: {}", 200, description.length());
            throw new ValidationException("Максимальное количество букв в описании фильма не должно превышать 200");

        } else if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Проверка на корректность даты фильма, releaseDate: {}", releaseDate);
            throw new ValidationException("Некорректная дата фильма: " + releaseDate);

        } else if (duration < 1) {
            log.info("Проверка на корректность продолжительности фильма, duration: {}", duration);
            throw new ValidationException("Продолжительность фильма должна быть положительной: " + duration);
        } else if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
    }

    public void validateUser(User user) {
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
    }

    public void validateFilmId(int filmsSize, int id) {
        log.info("Проверка на корректность id фильма: {}", id);
        if (id < 0 || id > filmsSize + 1) {
            throw new ValidationException("Некорректный идентификатор фильма.");
        }
    }

    public void validateUserId(int usersSize, int id) {
        log.info("Проверка на корректность id пользователя: {}", id);
        if (id < 0 || id > usersSize + 1) {
            throw new ValidationException("Некорректный id пользователя: " + id);
        }
    }

}
