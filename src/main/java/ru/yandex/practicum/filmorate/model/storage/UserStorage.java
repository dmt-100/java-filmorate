package ru.yandex.practicum.filmorate.model.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Set;

public interface UserStorage {
    Set<User> getUsers();

    User getUserById(int id);

    User createUser(User user);

    User putUser(User user);
}
