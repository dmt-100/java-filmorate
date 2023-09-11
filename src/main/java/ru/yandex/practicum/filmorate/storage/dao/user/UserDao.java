package ru.yandex.practicum.filmorate.storage.dao.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDao {

    User createUser(User user);

    User getUserById(int id);

    List<User> allUsers();

    User updateUser(User user);

    void deleteFriend(int id, int friendId);

    void addFriend(int id, int friendId);

    List<User> getUserFriends(int id);

    List<User> getCommonFriendList(int id, int friendId);
}
