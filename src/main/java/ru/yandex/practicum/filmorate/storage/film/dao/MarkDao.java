package ru.yandex.practicum.filmorate.storage.film.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mark;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MarkDao {

    List<Integer> findLikes(Film film);

    List<Mark> findMarks(Film film);

    Map<Integer, Set<Integer>> findAllUsersWithPositiveMarks();

}
