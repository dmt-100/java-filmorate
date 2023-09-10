package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);

    User getUserById(long id);

    List<User> allUsers();

    User updateUser(User user);

    void deleteFriend(long id, long friendId);

    void addFriend(long id, long friendId);

    List<User> getUserFriends(long id);

    List<User> getCommonFriendList(long id, long friendId);
}
