package ru.yandex.practicum.filmorate.model.storage;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    /*
    Создайте FilmService, который будет отвечать за операции с фильмами, — добавление и удаление лайка, вывод 10 наиболее популярных фильмов по количеству лайков. Пусть пока каждый пользователь может поставить лайк фильму только один раз.
     */
    FilmInMemoryStorage storage;

    public FilmService(FilmInMemoryStorage storage) {
        this.storage = storage;
    }

    public Set<Film> getMostPopularFilms(int count) {
        Set<Film> result;
        if (count == 1) {
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
        Film film = storage.getFilmById(id);
        storage.getFilmById(id).getLikes().remove(userId);
    }

    public Film addLike(int id, int userId) {
        Film film = storage.getFilmById(id);
        film.getLikes().add(userId);
        return film;
    }

}
