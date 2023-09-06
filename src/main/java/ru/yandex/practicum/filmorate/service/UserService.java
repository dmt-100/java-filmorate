package ru.yandex.practicum.filmorate.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Slf4j
@Service
public class UserService implements UserStorage {

    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int id, int friendId) {
        if (id != friendId) {
            userStorage.addFriend(id, friendId);
        } else {
            log.warn("Ошибка при добавлении друга.");
            throw new ResourceNotFoundException("Ошибка добавления друга, проверьте корректность данных.");
        }
    }

    public void deleteFriend(int id, int friendId) {
        if (id != friendId) {
            userStorage.deleteFriend(id, friendId);
        } else {
            log.warn("Ошибка при удалении друга.");
            throw new ValidationException("Ошибка удаления друга, проверьте корректность данных.");
        }
    }

    public List<User> getUserFriends(int id) {
        try {
            return userStorage.getUserFriends(id);
        } catch (ValidationException e) {
            log.warn("Ошибка при получении списка друзей.");
            throw new ValidationException("Ошибка списка друзей, проверьте корректность данных.");
        }
    }

    @Override
    public List<User> getCommonFriendList(int id, int friendId) {
        return userStorage.getCommonFriendList(id, friendId);
    }

    @Override
    public List<User> listUsers() {
        return userStorage.listUsers();
    }

    @Override
    public User getUserById(int id) {
        try {
            return userStorage.getUserById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Ошибка запроса пользователя, проверьте корректность данных.");
        }
    }

    @Override
    public User createUser(@NonNull User user) {
        if (userValidation(user)) {
            if (user.getName().isBlank())
                user.setName(user.getLogin());
            log.debug("Сохранен пользователь: {}", user);
            return userStorage.createUser(user);
        } else {
            log.warn("Ошибка при создании пользователя: {}", user);
            throw new ValidationException("Ошибка создания пользователя, проверьте корректность данных.");
        }
    }

    @Override
    public User updateUser(@NonNull User user) {
        if (getUserById(user.getId()).getId() == user.getId() && userValidation(user)) {
            log.debug("Обновлен пользователь: {}", user);
            return userStorage.updateUser(user);
        } else {
            log.warn("Ошибка при обновлении пользователя.");
            throw new ResourceNotFoundException("Ошибка обновления пользователя, проверьте корректность данных.");
        }
    }

    public boolean userValidation(User user) {
        return !user.getLogin().contains(" ");
    }
}


