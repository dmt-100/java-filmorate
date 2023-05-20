package ru.yandex.practicum.filmorate.model.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.service.UserValidator;
import ru.yandex.practicum.filmorate.model.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.model.storage.UserService;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final InMemoryUserStorage storage = new InMemoryUserStorage();
    private final UserService userService = new UserService();
    private UserValidator userValidator;

    @GetMapping
    public Set<User> getUsers() { // получение всех фильмов.
        return storage.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) { // получение всех фильмов.
        userValidator = new UserValidator(storage);
        userValidator.validateIdUsersSize(id);
        return storage.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsById(@PathVariable int id) {
        userValidator.validateId(id);
        log.info("Все друзья пользователя {}", storage.getUserById(id).getFriends());
        return userService.getFriends(storage, id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriendsById(@PathVariable int id, @PathVariable int otherId) {
        Set<User> friends;
        userValidator.validateId(id);
        userValidator.validateId(otherId);
        friends = userService.getCommonFriendsById(storage, id, otherId);
        log.info("Количество друзей пользователя с id {}, {}", id, storage.getUserById(id).getFriends().size());
        return friends;
    }

    @PostMapping
    public User createUser(@RequestBody User user) { // добавление фильма
        log.info("Пришёл запрос на добавление пользователя {}, количество пользователей: {}", user, getUsers().size());
        userValidator = new UserValidator();
        if (userValidator.validate(storage, user)) {
            storage.createUser(user);
            log.info("Добавление пользователя {}, количество пользователей: {}", user, getUsers().size());
        }
        return user;
    }

    @PutMapping
    public User putUser(@RequestBody User user) {
        log.info("Пришёл запрос на обновление пользователя {}", user);
        userValidator = new UserValidator();
        User result = null;
        if (userValidator.validate(storage, user)) {
            result = storage.putUser(user);

        }
        return result;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id, @PathVariable int friendId) {
        User user;
        userValidator.validateId(id);
        userValidator.validateId(friendId);
        log.info("Перед добавлением к пользователю {}\n друга {}", storage.getUserById(id),
                storage.getUserById(friendId));
        user = userService.addFriend(storage, id, friendId);
        log.info("После добавления к пользователю {}\n друга {}", storage.getUserById(id),
                storage.getUserById(friendId));
        return user;
    }

    /*
    PUT /users/{id}/friends/{friendId} — добавление в друзья.
    DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    */


    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        User user;
        log.info("Перед удалением у пользователя {}\n друга {}", storage.getUserById(id),
                storage.getUserById(friendId));
        user = userService.deleteFriend(storage, id, friendId);
        log.info("После удаления у пользователя {}\n друга {}", storage.getUserById(id),
                storage.getUserById(friendId));
        return user;
    }

    public InMemoryUserStorage getStorage() {
        return storage;
    }
}
