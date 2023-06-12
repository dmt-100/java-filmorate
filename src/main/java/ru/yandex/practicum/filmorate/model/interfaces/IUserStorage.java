package ru.yandex.practicum.filmorate.model.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Set;

public interface IUserStorage {

    Set<User> getUsers();

    User getUserById(int id);

    User createUser(User user);

    User putUser(User user);
}
