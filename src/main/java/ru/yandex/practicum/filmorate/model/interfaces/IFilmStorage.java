package ru.yandex.practicum.filmorate.model.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

public interface IFilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Set<Film> getFilms();

    Film getFilmById(int id);
}
