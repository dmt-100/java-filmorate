package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.dao.FriendListDao;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FriendListDao friendListDao;

    @Autowired
    public UserService(UserStorage userStorage, FriendListDao friendListDao, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.friendListDao = friendListDao;
        this.filmStorage = filmStorage;
    }

    public void createUser(User user) {
        userStorage.createUser(user);

    }

    public void deleteUser(int id) {
        userStorage.deleteUser(id);
    }

    public void addFriend(int userId, int friendId) {

        friendListDao.addFriend(userId, friendId);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public void updateUser(User userToUpdate) {
        userStorage.updateUser(userToUpdate);
    }

    public User findUser(int id) {
        return userStorage.findUser(id);
    }

    public List<User> getAllFriend(int userId) {

        return friendListDao.getAll(userId);
    }

    public void delFriend(int userId, int friendId) {

        friendListDao.deleteFriend(userId, friendId);
    }

    public List<User> getCommonFriends(int userId, int friendId) {
        return friendListDao.getCommonFriends(userId, friendId);
    }

    public List<Film> findRecommendedFilms(int id) {
        return filmStorage.getRecommendations(id);
    }
}
