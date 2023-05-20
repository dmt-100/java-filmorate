package ru.yandex.practicum.filmorate.model.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.service.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.service.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.storage.InMemoryUserStorage;

import java.time.LocalDate;

@Slf4j
public class UserValidator {

    InMemoryUserStorage inMemoryUserStorage;

    private String login;
    private String email;
    private LocalDate birthDay;
    private String name;
    private int id;

    public UserValidator(User user, InMemoryUserStorage inMemoryUserStorage) {
        this.login = user.getLogin();
        this.email = user.getEmail();
        this.birthDay = user.getBirthday();
        this.name = user.getName();
        this.id = user.getId();
        this.inMemoryUserStorage = inMemoryUserStorage;
    }
    public UserValidator(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public UserValidator() {
    }
    public boolean validate(InMemoryUserStorage inMemoryUserStorage, User user) {
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

        } else if (user.getId() > inMemoryUserStorage.getUsers().size() + 1) {
            log.info("Проверка на корректность id пользователя, id: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id пользователя: " + id);
        }

        return true;
    }

    public void validateIdUsersSize(int id) {
        log.info("Проверка на корректность id пользователя, id: {}", id);
        if (id > inMemoryUserStorage.getUsers().size() + 1) {
            throw new ResourceNotFoundException("Некорректный id пользователя: " + id);
        }
    }

    public void validateId(int id) {
        log.info("Проверка на корректность id пользователя, id: {}", id);
        if (id < 0) {
            throw new ResourceNotFoundException("Некорректный id пользователя: " + id);
        }
    }
}
