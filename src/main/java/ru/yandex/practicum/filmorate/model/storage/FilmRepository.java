package ru.yandex.practicum.filmorate.model.storage;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;
import java.util.Set;

@Data
public class FilmRepository {

    private final Set<Film> films = new HashSet<>();

    public Film getEqual(Film film, Set<Film> films) {
        return films.stream().filter(film::equals).findAny().orElse(null);
    }

    public Film getFilmById(int id) {
        Film film = films
                .stream()
                .filter(f -> f.getId() == id)
                .findFirst()
                .orElse(null);
        return film;
    }

}