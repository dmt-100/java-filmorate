package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film createFilm(Film film);

    Film getFilmById(long id);

    List<Film> allFilms();

    Film updateFilm(Film film);

    void deleteFilm(long id);

    void deleteLike(long id, long userId);

    void addLike(long id, long userId);

    List<Film> getMostPopularFilms(int count);
}
