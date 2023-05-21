package ru.yandex.practicum.filmorate.model.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.model.service.Validator;
import ru.yandex.practicum.filmorate.model.service.IdCounter;
import ru.yandex.practicum.filmorate.model.exception.ValidationException;

import java.util.*;

@Slf4j
@Component
public class FilmInMemoryStorage implements FilmStorage {

    private final FilmRepository repository = new FilmRepository();

    @Override
    public Film addFilm(Film film) {
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
            Film f = repository.getFilmById(film.getId());

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

    @Override
    public Set<Film> getFilms() {
        return repository.getFilms();
    }

    @Override
    public Film getFilmById(int id) {
        Validator.validateFilmId(id);
        return repository.getFilmById(id);
    }

}
