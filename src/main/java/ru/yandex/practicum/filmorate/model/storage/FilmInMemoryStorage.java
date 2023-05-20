package ru.yandex.practicum.filmorate.model.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class FilmInMemoryStorage implements FilmStorage {

    private final FilmRepository filmRepository = new FilmRepository();

    @Override
    public Set<Film> getFilms() {
        return filmRepository.getFilms();
    }

    @Override
    public Film getFilmById(int id) {
        return filmRepository.getFilmById(id);
    }

}
