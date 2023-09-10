package ru.yandex.practicum.filmorate.storage.dao.film;

public interface LikeDao {
    void addLikeToFilm(long id, long userId);

    void deleteLikeFromFilm(long id, long userId);
}
