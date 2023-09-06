package ru.yandex.practicum.filmorate.model.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.interfaces.IFilmStorage;
import ru.yandex.practicum.filmorate.model.service.Validator;
import ru.yandex.practicum.filmorate.model.service.IdCounter;
import ru.yandex.practicum.filmorate.model.exception.ValidationException;

import java.util.*;

@Slf4j
@Component
public class FilmInMemoryStorage implements IFilmStorage {
    private final Set<Film> films = new HashSet<>();

    public Film getEqual(Film film) {
        return films.stream().filter(film::equals).findAny().orElse(null);
    }

    @Override
    public Set<Film> getFilms() {
        return films;
    }

    @Override
    public Film getFilmById(int id) {
        Validator.validateFilmId(films.size(), id);
        Film film = films
                .stream()
                .filter(f -> f.getId() == id)
                .findFirst()
                .orElse(null);
        return film;
    }

    @Override
    public Film createFilm(Film film) {
        if (getFilms().contains(film)) {
            throw new ValidationException("Фильм " + film + ", уже есть в коллекции.");
        }
        if (Validator.validateFilm(film)) {
            film.setId(IdCounter.increaseFilmId());
            if (film.getLikes() == null) {
                film.setLikes(new HashSet<>());
            }
            getFilms().add(film);
            log.info("Добавление фильма {}, количество фильмов: {}", film, getFilms().size());
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Film filmUpdate = null;
        if (Validator.validateFilm(film)) {
            Validator.validateFilmId(films.size(), film.getId());
            Film f = getFilmById(film.getId());

            f.setName(film.getName());
            f.setDescription(film.getDescription());
            f.setReleaseDate(film.getReleaseDate());
            f.setDuration(film.getDuration());
            f.setRate(film.getRate());
            f.setLikes(film.getLikes());

            filmUpdate = f;
        }
        return filmUpdate;
    }

}
