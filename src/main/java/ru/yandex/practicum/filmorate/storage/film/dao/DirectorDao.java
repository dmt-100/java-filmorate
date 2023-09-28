package ru.yandex.practicum.filmorate.storage.film.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorDao {
    Collection<Director> getAllDirectors();

    Director getDirectorById(Integer directorId);

    void deleteDirectorById(Integer directorId);

    Director createDirector(Director director);

    Director updateDirector(Director director);

}
