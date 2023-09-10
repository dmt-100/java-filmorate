package ru.yandex.practicum.filmorate.storage.dao.user;

public interface FriendDao {
    void addFriend(long id, long friendId);

    void deleteFriend(long id, long friendId);
}
