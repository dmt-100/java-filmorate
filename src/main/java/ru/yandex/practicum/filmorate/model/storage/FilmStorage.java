package ru.yandex.practicum.filmorate.model.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

public interface FilmStorage {

    Set<Film> getFilms();
    Film getFilmById(int id);
}
