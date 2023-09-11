package ru.yandex.practicum.filmorate.storage.dao.film;

public interface LikeDao {
    void addLikeToFilm(int id, int userId);

    void deleteLikeFromFilm(int id, int userId);
}
