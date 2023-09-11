package ru.yandex.practicum.filmorate.storage.dao.user;

public interface FriendDao {
    void addFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);
}
