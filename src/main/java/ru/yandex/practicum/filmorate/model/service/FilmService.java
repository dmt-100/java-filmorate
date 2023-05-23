package ru.yandex.practicum.filmorate.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.storage.FilmInMemoryStorage;
import ru.yandex.practicum.filmorate.model.storage.UserInMemoryStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmInMemoryStorage filmStorage;
    private final UserInMemoryStorage userStorage;

    @Autowired
    public FilmService(FilmInMemoryStorage filmStorage, UserInMemoryStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Set<Film> getMostPopularFilms(int count) {
        Set<Film> result;
        if (count <= 0) {
            throw new ResourceNotFoundException("Некорректный count: " + count);
        } else if (count == 1) {
            result = filmStorage.getFilms()
                    .stream().max(Comparator.comparing(f -> f.getLikes().size()))
                    .stream()
                    .collect(Collectors.toCollection(HashSet::new));
        } else {
            result = filmStorage.getFilms()
                    .stream()
                    .sorted(Comparator.comparing(f -> f.getLikes().size()))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        return result;
    }

    public void removeLike(int id, int userId) {
        Validator.validateFilmId(filmStorage.getFilms().size(), id);
        Validator.validateUserId(userStorage.getUsers().size(), userId);
        filmStorage.getFilmById(id).getLikes().remove(userId);
    }

    public Film addLike(int id, int userId) {
        Validator.validateFilmId(filmStorage.getFilms().size(), id);
        Validator.validateUserId(userStorage.getUsers().size(), userId);
        Film film = filmStorage.getFilmById(id);
        film.getLikes().add(userId);
        return film;
    }

}
