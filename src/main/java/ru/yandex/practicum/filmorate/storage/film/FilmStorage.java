package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAll();

    Film createFilm(@RequestBody Film film);

    Film updateFilm(Film film);

    void deleteFilm(@PathVariable int id);

    Film findFilm(@PathVariable int id);

    Collection<Film> findAllPopularFilms(String query, String by);

    Collection<Film> findPopularFilms(Integer count, Integer genreId, Integer year);

    Collection<Film> findListOfCommonFilms(int userId, int friendId);

    Optional<Film> findFilmById(int filmId);

    List<Film> getRecommendations(int id);
}
