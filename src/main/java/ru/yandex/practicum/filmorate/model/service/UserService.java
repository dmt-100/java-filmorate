package ru.yandex.practicum.filmorate.model.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.storage.UserInMemoryStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserInMemoryStorage storage;

    public UserService(UserInMemoryStorage storage) {
        this.storage = storage;
    }

    public User addFriend(int id, int friendId) {
        Validator.validateUserId(storage.getUsers().size(), id);
        Validator.validateUserId(storage.getUsers().size(), friendId);
        storage.getUserById(id).getFriends().add(friendId);
        storage.getUserById(friendId).getFriends().add(id);

        return storage.getUserById(id);
    }

    public User deleteFriend(int id, int friendId) {
        storage.getUserById(id).getFriends().remove(friendId);
        storage.getUserById(friendId).getFriends().remove(id);
        return storage.getUserById(id);
    }

    public Set<User> getCommonFriendsById(int id, int otherId) {
        Validator.validateUserId(storage.getUsers().size(), id);
        Validator.validateUserId(storage.getUsers().size(), otherId);
        Set<User> result = new LinkedHashSet<>();
        if (storage.getUserById(id).getFriends().size() == 0) {
            return result;
        }
        for (Integer friend1 : storage.getUserById(id).getFriends()) {
            for (Integer friend2 : storage.getUserById(otherId).getFriends()) {
                if (Objects.equals(friend1, friend2)) {
                    result.add(storage.getUserById(friend1));
                }
            }
        }
        return result;
    }

    public List<User> getFriends(int id) {
        Validator.validateUserId(storage.getUsers().size(), id);

        return storage.getUserById(id)
                .getFriends()
                .stream()
                .map(storage::getUserById)
                .collect(Collectors.toList());
    }

    public Set<User> getUsers() {
        return storage.getUsers();
    }

    public User getUserById(@PathVariable int id) {
        return storage.getUserById(id);
    }


    public User createUser(@RequestBody User user) {
        storage.createUser(user);
        return user;
    }

    public User putUser(@RequestBody User user) {
        return storage.putUser(user);
    }

}
