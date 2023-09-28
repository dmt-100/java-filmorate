package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.dao.DirectorDao;
import ru.yandex.practicum.filmorate.storage.film.dao.LikeDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeDao likesDao;
    private final DirectorDao directorDao;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, LikeDao likesDao, DirectorDao directorDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likesDao = likesDao;
        this.directorDao = directorDao;
    }

    public void createFilm(Film film) {
        validation(film);
        filmStorage.createFilm(film);
    }

    public Film updateFilm(Film filmToUpdate) {
        validation(filmToUpdate);
        return filmStorage.updateFilm(filmToUpdate);
    }

    public void deleteFilm(int filmToDelite) {
        filmStorage.deleteFilm(filmToDelite);
    }

    public Film findFilm(int id) {
        return filmStorage.findFilm(id);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public void addLike(int filmId, Integer userId) {
        likesDao.addLikeToFilm(filmId, userId);
    }

    public void deleteLike(int filmId, Integer userId) {
        likesDao.deleteLikeFromFilm(filmId, userId);
    }

    public int getAllLikes(int filmId) {
        return filmStorage.findFilm(filmId).getLikes().size();
    }

    public Collection<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        return filmStorage.findPopularFilms(count, genreId, year);
    }

    public Collection<Film> getListOfCommonFilms(int userId, int friendId) {
        return filmStorage.findListOfCommonFilms(userId, friendId);
    }

    private void validation(Film film) {
        final LocalDate latestReleaseDate = LocalDate.of(1895, 12, 28);

        if (film.getReleaseDate().isBefore(latestReleaseDate)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года.");
        }
        if (film.getName().isEmpty() && film.getName().isBlank()) {
            throw new ValidationException("name cod not be blank or empty");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("duration mast be positive ");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("duration must be less then 200");
        }
    }

    public Collection<Film> getSortFilmByDirector(Integer directorId, String sortBy) {
        Director director = directorDao.getDirectorById(directorId);
        if (sortBy.equals("year") || sortBy.equals("likes")) {
            return filmStorage.findAll().stream()
                    .filter(film -> film.getDirectors().contains(director))
                    .sorted((f1, f2) -> {
                        if (sortBy.equals("year")) {
                            return f1.getReleaseDate().compareTo(f2.getReleaseDate());
                        } else {
                            return f2.getLikes().size() - f1.getLikes().size();
                        }
                    })
                    .collect(Collectors.toList());
        } else {
            throw new ValidationException("Некорректный запрос на sort" + sortBy);
        }
    }

    public Collection<Film> getAllPopularFilms(String query, String by) {
        return filmStorage.findAllPopularFilms(query, by);
    }
}
