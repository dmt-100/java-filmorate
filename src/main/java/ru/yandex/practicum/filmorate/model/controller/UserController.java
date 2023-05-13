package ru.yandex.practicum.filmorate.model.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.repository.UserRepository;
import ru.yandex.practicum.filmorate.model.service.IdCounter;
import ru.yandex.practicum.filmorate.model.service.UserValidator;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    UserRepository userRepository = new UserRepository();
    UserValidator userValidator = new UserValidator();

    @GetMapping
    public Set<User> getUsers() { // получение всех фильмов.
        return userRepository.getUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) { // добавление фильма
        log.info("Пришёл запрос на добавление пользователя {}", user);
        if (userValidator.validate(userRepository, user)) {
            user.setId(IdCounter.increaseUserId());
            getUsers().add(user);
            log.info("Добавление пользователя {}, количество пользователей: {}", user, getUsers().size());
        }
        return user;
    }

    @PutMapping
    public User putUser(@RequestBody User user) {
        log.info("Пришёл запрос на обновление пользователя {}", user);
        User result = null;
        if (userValidator.validate(userRepository, user)) {
            for (User u : getUsers()) {
                if (u.getId() == user.getId()) {
                    u.setEmail(user.getEmail());
                    u.setLogin(user.getLogin());
                    u.setName(user.getName());
                    u.setBirthday(user.getBirthday());
                } else {
                    throw new ValidationException("Некорректный Id: " + user.getId());
                }
            }
            for (User user1 : getUsers()) {
                if (user1.equals(user)) {
                    result = user1;
                }
            }
        }
        return result;
    }
}
