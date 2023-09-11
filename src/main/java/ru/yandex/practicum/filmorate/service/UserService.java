package ru.yandex.practicum.filmorate.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.user.UserDaoStorage;

import java.util.List;

@Slf4j
@Service
public class UserService implements UserServiceImpl {

    private final UserDaoStorage userDaoStorage;
    private final Validator validator;
    private final UserIdCounter userIdCounter;

    public UserService(@Qualifier("userDaoStorage") UserDaoStorage userDaoStorage, Validator validator, UserIdCounter userIdCounter) {
        this.userDaoStorage = userDaoStorage;
        this.validator = validator;
        this.userIdCounter = userIdCounter;
    }


    @Override
    public User createUser(@NonNull User user) {
        try {
            validator.validateUser(user);
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            log.debug("Сохранен пользователь: {}", user);
            return userDaoStorage.createUser(user);
        } catch (ValidationException e) {
            log.warn("Ошибка при создании пользователя: {}", user);
            throw new ValidationException("Ошибка создания пользователя, проверьте корректность данных.");
        }
    }

    @Override
    public User getUserById(int id) {
        try {
            return userDaoStorage.getUserById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Ошибка запроса пользователя, проверьте корректность данных.");
        }
    }

    @Override
    public List<User> allUsers() {
        return userDaoStorage.allUsers();
    }

    public void addFriend(int id, int friendId) {
        if (id != friendId) {
            userDaoStorage.addFriend(id, friendId);
        } else {
            log.warn("Ошибка при добавлении друга.");
            throw new ResourceNotFoundException("Ошибка добавления друга, проверьте корректность данных.");
        }
    }

    public void deleteFriend(int id, int friendId) {
        if (id != friendId) {
            userDaoStorage.deleteFriend(id, friendId);
        } else {
            log.warn("Ошибка при удалении друга.");
            throw new ValidationException("Ошибка удаления друга, проверьте корректность данных.");
        }
    }

    public List<User> getUserFriends(int id) {
        try {
            return userDaoStorage.getUserFriends(id);
        } catch (ValidationException e) {
            log.warn("Ошибка при получении списка друзей.");
            throw new ValidationException("Ошибка списка друзей, проверьте корректность данных.");
        }
    }

    @Override
    public List<User> getCommonFriendList(int id, int friendId) {
        return userDaoStorage.getCommonFriendList(id, friendId);
    }

    @Override
    public User updateUser(@NonNull User user) {
        try {
            validator.validateUser(user);
            validator.validateUserId(userDaoStorage.allUsers().size(), user.getId());
            int userIdInStorage = getUserById(user.getId()).getId();
            int userId = user.getId();
            if (userIdInStorage == userId) {
                userDaoStorage.updateUser(user);
                log.debug("Обновлен пользователь: {}", user);
            }
        } catch (ResourceNotFoundException e) {
            log.warn("Ошибка при обновлении пользователя.");
            throw new ResourceNotFoundException("Ошибка обновления пользователя, проверьте корректность данных.");
        }
        return userDaoStorage.updateUser(user);
    }

}


