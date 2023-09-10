package ru.yandex.practicum.filmorate.storage.memory;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmIdCounter;
import ru.yandex.practicum.filmorate.service.Validator;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Data
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private Map<Long, List<Long>> likes = new HashMap<>();
    private final InMemoryUserStorage inMemoryUserStorage;
    private final Validator validator;
    private final FilmIdCounter filmIdCounter;

    @Override
    public List<Film> allFilms() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(long id) {
        log.debug("Текущий фильм {}", films.get(id));
        return films.get(id);
    }

    @Override
    public Film createFilm(@NonNull Film film) {
        film.setId(filmIdCounter.increaseFilmId());
        if (validator.validateFilm(film)) {
            films.put(film.getId(), film);
            log.debug("Сохранен фильм: {}", film);
        }
        return film;
    }

    @Override
    public Film updateFilm(@NonNull Film film) {
        if (validator.validateFilm(film) && validator.validateFilmId(films.size(), film.getId())) {
            films.put(film.getId(), film);
        }
        return film;
    }

    @Override
    public void deleteFilm(long id) {

    }

    @Override
    public void addLike(long id, long userId) {
        if (idValidation(id) && userId > 0 && inMemoryUserStorage.getUsers().containsKey(userId)) {
            Film film = films.get(id);
            List<Long> filmLikes = likes.get(id);
            filmLikes.add(userId);
            likes.put(id, filmLikes);
            log.debug("Фильму {} поставили лайк.", film);
        } else {
            log.warn("Ошибка при добавлении лайка фильму.");
            throw new ValidationException("Ошибка добавления лайка, проверьте корректность данных.");
        }
    }

    @Override
    public void deleteLike(long id, long userId) {
        if (idValidation(id) && likes.get(id).contains(userId)) {
            Film film = films.get(id);
            likes.get(id).remove(userId);
            log.debug("Фильму {} удалили лайк.", film);
        } else {
            log.warn("Ошибка при удалении лайка фильму.");
            throw new ResourceNotFoundException("Ошибка удаления лайка, проверьте корректность данных.");
        }
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        if (count > 0) {
            List<Long> likesSorted = likes.entrySet()
                    .stream()
                    .sorted(Comparator.comparingInt(e -> e.getValue().size()))
                    .limit(count)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            List<Film> sortedFilms = new ArrayList<>();
            for (Long v : likesSorted) {
                sortedFilms.add(films.get(v));
            }
            return sortedFilms;

        } else {
            log.warn("Ошибка запроса списка популярных фильмов.");
            throw new ValidationException("Ошибка запроса списка популярных фильмов, проверьте корректность данных.");
        }
    }

    private boolean idValidation(@NonNull long id) {
        return films.containsKey(id);
    }
}
