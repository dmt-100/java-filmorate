package ru.yandex.practicum.filmorate.model.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.storage.FilmInMemoryStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmInMemoryStorage storage;

    public FilmService(FilmInMemoryStorage storage) {
        this.storage = storage;
    }

    public Set<Film> getMostPopularFilms(int count) {
        Set<Film> result;
        if (count <= 0) {
            throw new ResourceNotFoundException("Некорректный count: " + count);
        } else if (count == 1) {
            result = storage.getFilms()
                    .stream().max(Comparator.comparing(f -> f.getLikes().size()))
                    .stream()
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } else {
            result = storage.getFilms()
                    .stream()
                    .sorted(Comparator.comparing(f -> f.getLikes().size()))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        return result;
    }

    public void removeLike(int id, int userId) {
        Validator.validateFilmId(id);
        Validator.validateUserId(userId);
        storage.getFilmById(id).getLikes().remove(userId);
    }

    public Film addLike(int id, int userId) {
        Validator.validateFilmId(id);
        Validator.validateUserId(userId);
        Film film = storage.getFilmById(id);
        film.getLikes().add(userId);
        return film;
    }

}
