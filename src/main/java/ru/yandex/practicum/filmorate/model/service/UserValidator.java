package ru.yandex.practicum.filmorate.model.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.service.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.service.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.storage.UserInMemoryStorage;

import java.time.LocalDate;
import java.util.HashSet;

@Slf4j
public class UserValidator {

    UserInMemoryStorage storage;

    public UserValidator(UserInMemoryStorage storage) {
        this.storage = storage;
    }

    public boolean validate(User user) {
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

        } else if (user.isEmptyName()) {
            log.info("Проверка поля name на null, имя пользователя: {}", name);
            user.setName(user.getLogin());
            log.info("Обновленное имя пользователя: {}", user.getName());

        } else if (storage.getUserById(id).getFriends() == null) {
            storage.getUserById(id).setFriends(new HashSet<>());

        } else {
            validateId(id);
        }
        return true;
    }

    public void validateId(int id) {
        log.info("Проверка на корректность id пользователя, id: {}", id);
        if (id < 0 || id > storage.getUsers().size() + 1) {
            throw new ResourceNotFoundException("Некорректный id пользователя: " + id);
        }
    }
}
