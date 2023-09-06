package ru.yandex.practicum.filmorate.dao;

public interface FriendDao {
    void addFriend(int id, int friendId);
    void deleteFriend(int id, int friendId);
}
