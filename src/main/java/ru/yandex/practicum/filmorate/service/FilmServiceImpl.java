package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmServiceImpl {

    Film createFilm(Film film);

    Film getFilmById(int id);

    List<Film> allFilms();

    Film updateFilm(Film film);

    void deleteFilm(int id);

    void deleteLike(int id, int userId);

    void addLike(int id, int userId);

    List<Film> getMostPopularFilms(int count);
}
