package ru.yandex.practicum.filmorate.model.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.service.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.storage.FilmService;
import ru.yandex.practicum.filmorate.model.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.model.service.FilmValidator;
import ru.yandex.practicum.filmorate.model.service.IdCounter;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
    private final FilmService filmService = new FilmService();
    private final FilmValidator filmValidator = new FilmValidator();

    @GetMapping
    public Set<Film> getFilms() { // получение всех фильмов.
        return inMemoryFilmStorage.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) { // получение всех фильмов.
        log.info("Получение фильма по id: {}", id);
        return inMemoryFilmStorage.getFilmById(id);
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) { // добавление фильма
        log.info("Добавление фильма {}", film);
        film.setId(IdCounter.increaseFilmId());
        if (getFilms().contains(film)) {
            throw new ValidationException("Фильм " + film + ", уже есть в коллекции.");
        }
        if (filmValidator.validate(inMemoryFilmStorage, film)) {
            getFilms().add(film);
            log.info("Добавление фильма {}, количество фильмов: {}", film, getFilms().size());
        }
        return film;
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) {
        Film result = null;
        if (filmValidator.validate(inMemoryFilmStorage, film)) {
            for (Film f : inMemoryFilmStorage.getFilms()) {
                if (f.getId() == film.getId()) {
                    f.setName(film.getName());
                    f.setDescription(film.getDescription());
                    f.setReleaseDate(film.getReleaseDate());
                    f.setDuration(film.getDuration());
                    f.setRate(film.getRate());
                    result = f;
                }
            }
        }
        return result;
    }

    @PutMapping("/{id}/like/{userId}")
    public Film  addLikeToFilm(@PathVariable int id, @PathVariable int userId) {
        log.info("Добавление лайка фильму с id: {}, от пользователя id: {}", id, userId);
        return inMemoryFilmStorage.addLikeToFilm(id, userId);
    }

    @GetMapping("/popular?count={count}")
    public List<Film> getPopularFilms(@RequestParam(value = "count", required=false)@PathVariable Integer count) {
        log.info("Получение наиболее популярных фильмов: {}", count);
        List<Film> result;
        result = filmService.getMostPopularFilms(Objects.requireNonNullElse(count, 10));
        return result;
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms2() {
        log.info("Получение наиболее популярных фильмов: {}", 10);
        List<Film> result;
        result = filmService.getMostPopularFilms(10);
        return result;
    }

    public InMemoryFilmStorage getInMemoryFilmStorage() {
        return inMemoryFilmStorage;
    }
}
