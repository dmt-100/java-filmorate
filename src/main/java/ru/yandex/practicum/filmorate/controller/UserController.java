package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        userService.createUser(user);
        return user;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        log.info("Пользователь с id {} был удален", id);
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User userToUpdate) {
        userService.updateUser(userToUpdate);
        return userToUpdate;
    }

    @GetMapping("/{id}")
    public User findUser(@PathVariable int id) {
        return userService.findUser(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") int userId, @PathVariable("friendId") int friendId) {
        userService.addFriend(userId, friendId);
        log.info("Пользователь с id {} добавил в друзья пользователя с id {}", userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriend(@PathVariable int id) {
        return userService.getAllFriend(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") int userId, @PathVariable("friendId") int friendId) {
        userService.delFriend(userId, friendId);
        log.info("Пользователь с id {} удалил из друзей пользователя с id {}", userId, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> commonFriends(@PathVariable("id") int id, @PathVariable("otherId") int otherId) {
        log.info("Пользователь с id {} ищет общих друзей у пользователя с id {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping(value = "/{id}/recommendations")
    public List<Film> findRecommendedFilms(@PathVariable int id) {
        return userService.findRecommendedFilms(id);
    }

}
