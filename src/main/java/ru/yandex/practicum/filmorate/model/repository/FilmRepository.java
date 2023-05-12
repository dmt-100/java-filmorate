package ru.yandex.practicum.filmorate.model.repository;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;
import java.util.Set;

@Data
public class FilmRepository {

    private Set<Film> films = new HashSet<>();

    Film getEqual(Film film, Set<Film> films) {
        return films.stream().filter(film::equals).findAny().orElse(null);
    }



}
