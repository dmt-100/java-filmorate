package ru.yandex.practicum.filmorate.model.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.storage.UserInMemoryStorage;
import ru.yandex.practicum.filmorate.model.service.UserService;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserInMemoryStorage storage;
    private final UserService userService;

    public UserController(UserInMemoryStorage storage, UserService userService) {
        this.storage = storage;
        this.userService = userService;
    }

    @GetMapping
    public Set<User> getUsers() {
        return storage.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        log.info("Пользователь {}", id);
        return storage.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsById(@PathVariable int id) {
        log.info("Друзья пользователя {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriendsById(@PathVariable int id, @PathVariable int otherId) {
        log.info("Количество обших друзей, друг1 {}, друг2 {}", id, otherId);
        return userService.getCommonFriendsById(id, otherId);
    }

    @PostMapping
    public User createUser(@RequestBody User user) { // добавление фильма
        log.info("Пришёл запрос на добавление пользователя {}, количество пользователей: {}", user, getUsers().size());
        storage.createUser(user);
        return user;
    }

    @PutMapping
    public User putUser(@RequestBody User user) {
        log.info("Обновление пользователя {}", user);
        return storage.putUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id, @PathVariable int friendId) {
        User user;
        log.info("Добавление друга, id пользователя {}, id друга {}", id, friendId);
        user = userService.addFriend(id, friendId);
        return user;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        User user;
        log.info("Удаление друга, id пользователя {}, id друга {}", id, friendId);
        user = userService.deleteFriend(id, friendId);
        return user;
    }

}
