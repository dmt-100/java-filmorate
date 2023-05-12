package ru.yandex.practicum.filmorate.model.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.repository.UserRepository;

import java.time.LocalDate;

@Slf4j
public class UserValidator {

    public boolean validate(UserRepository userRepository, User user) {

        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.info("Проверка поля login, user.getLogin().isBlank(): {}", user.getLogin().isBlank());
            log.info("Проверка поля login, user.getLogin().contains(\" \"): {}", user.getLogin().contains(" "));
            log.info("Проверка поля login, user.getLogin(): \"{}\"", user.getLogin());
            throw new ValidationException("У пользователя некорректный логин: " + user.getLogin());

        } else if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.info("Проверка поля email, user.getEmail().isBlank(): {}", user.getEmail().isBlank());
            log.info("Проверка поля email, user.getEmail().contains(\"@\"): {}", user.getEmail().contains("@"));
            log.info("Проверка поля email, user.getEmail(): {}", user.getEmail());
            throw new ValidationException("У пользователя некорректный емейл: " + user.getEmail());

        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Проверка поля email, user.getEmail(): {}", user.getEmail());
            throw new ValidationException("Некорректная дата рождения пользователя: " + user.getBirthday());

        } else if (user.isEmptyName()) {
            log.info("Проверка поля name на пустоту, user.getEmail().isBlank(): {}", user.getEmail().isBlank());
            log.info("Проверка поля name на пустоту, user.getEmail(): {}", user.getEmail());
            user.setName(user.getLogin());

        } else if (user.getId() > userRepository.getUsers().size() + 1) {
            log.info("Проверка на корректность id пользователя, user.getId(): {}", user.getId());
            throw new ValidationException("Некорректный id пользователя. Id: " + user.getId());
        }

        return true;
    }

}
