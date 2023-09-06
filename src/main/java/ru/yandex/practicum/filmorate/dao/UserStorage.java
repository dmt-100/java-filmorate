package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);

    User getUserById(int id);

    List<User> listUsers();

    User updateUser(User user);

    void deleteFriend(int id, int friendId);

    void addFriend(int id, int friendId);

    List<User> getUserFriends(int id);

    List<User> getCommonFriendList(int id, int friendId);
}
