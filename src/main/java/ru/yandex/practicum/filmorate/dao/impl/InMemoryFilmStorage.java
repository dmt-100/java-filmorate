package ru.yandex.practicum.filmorate.dao.impl;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exeption.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.IdCounter;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final InMemoryUserStorage inMemoryUserStorage;
    private int filmsIdCount = 0;
    private final Map<Integer, Film> films = new HashMap<>();
    private Map<Integer, List<Integer>> likes = new HashMap<>();


    @Override
    public List<Film> allFilms() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(int id) {
        log.debug("Текущий фильм {}", films.get(id));
        return films.get(id);
    }

    @Override
    public Film createFilm(@NonNull Film film) {
        addNewId(film);
        films.put(film.getId(), film);
        log.debug("Сохранен фильм: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(@NonNull Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            log.warn("Ошибка при обновлении фильма: {}", film);
            throw new ResourceNotFoundException("Ошибка изменения фильма, проверьте корректность данных.");
        }
    }

    @Override
    public void addLike(int id, int userId) {
        if (idValidation(id) && userId > 0 && inMemoryUserStorage.getUsers().containsKey(userId)) {
            Film film = films.get(id);
            List<Integer> filmLikes = likes.get(id);
            filmLikes.add(userId);
            likes.put(id, filmLikes);
            log.debug("Фильму {} поставили лайк.", film);
        } else {
            log.warn("Ошибка при добавлении лайка фильму.");
            throw new ValidationException("Ошибка добавления лайка, проверьте корректность данных.");
        }
    }

    @Override
    public void deleteLike(int id, int userId) {
        if (idValidation(id) && likes.get(id).contains(userId)) {
            Film film = films.get(id);
            likes.get(id).remove((Integer) userId);
            log.debug("Фильму {} удалили лайк.", film);
        } else {
            log.warn("Ошибка при удалении лайка фильму.");
            throw new ResourceNotFoundException("Ошибка удаления лайка, проверьте корректность данных.");
        }
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        if (count > 0) {
            List<Integer> likesSorted = likes.entrySet()
                    .stream()
                    .sorted(Comparator.comparingInt(e -> e.getValue().size()))
                    .limit(count)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            List<Film> sortedFilms = new ArrayList<>();
            for (Integer v : likesSorted) {
                sortedFilms.add(films.get(v));
            }
            return sortedFilms;

        } else {
            log.warn("Ошибка запроса списка популярных фильмов.");
            throw new ValidationException("Ошибка запроса списка популярных фильмов, проверьте корректность данных.");
        }
    }

    private void addNewId(Film film) {
        film.setId(IdCounter.increaseFilmId());
//        int id = filmsIdCount + 1;
//        while (films.containsKey(id)) {
//            id += id;
//        }
//        film.setId(id);
//        filmsIdCount = id;
    }

    private boolean idValidation(@NonNull int id) {
        return films.containsKey(id);
    }
}
