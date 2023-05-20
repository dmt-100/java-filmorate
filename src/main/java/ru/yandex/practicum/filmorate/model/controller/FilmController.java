package ru.yandex.practicum.filmorate.model.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.service.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.storage.FilmService;
import ru.yandex.practicum.filmorate.model.storage.FilmInMemoryStorage;
import ru.yandex.practicum.filmorate.model.service.FilmValidator;
import ru.yandex.practicum.filmorate.model.service.IdCounter;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmInMemoryStorage storage = new FilmInMemoryStorage();
    private final FilmService service = new FilmService(storage);
    private final FilmValidator validator = new FilmValidator(storage);

    @GetMapping
    public Set<Film> getFilms() { // получение всех фильмов.
        return storage.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) { // получение всех фильмов.
        log.info("Получение фильма по id: {}", id);
        validator.validateId(id);
        return storage.getFilmById(id);
    }

    @GetMapping("/popular")
    public Set<Film> getPopularFilms(@RequestParam(value = "count", required = false) @PathVariable Integer count) {
        log.info("Получение наиболее популярных фильмов: {}", count);
        Set<Film> result;
        result = service.getMostPopularFilms(Objects.requireNonNullElse(count, 10));
        return result;
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) { // добавление фильма
        log.info("Добавление фильма {}", film);
        if (getFilms().contains(film)) {
            throw new ValidationException("Фильм " + film + ", уже есть в коллекции.");
        }
        if (validator.validate(film)) {
            film.setId(IdCounter.increaseFilmId());
            getFilms().add(film);
            log.info("Добавление фильма {}, количество фильмов: {}", film, getFilms().size());
        }
        return film;
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) {
        Film result = null;
        if (validator.validate(film)) {
            Film f = storage.getFilmById(film.getId());

            f.setName(film.getName());
            f.setDescription(film.getDescription());
            f.setReleaseDate(film.getReleaseDate());
            f.setDuration(film.getDuration());
            f.setRate(film.getRate());
            if (film.getLikes() == null) {
                f.setLikes(new HashSet<>());
            }
            result = f;
        }
        return result;
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLikeToFilm(@PathVariable int id, @PathVariable int userId) {
        log.info("Добавление лайка фильму с id: {}, от пользователя id: {}", id, userId);
        validator.validateId(id);
        validator.validateId(userId);
        return service.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Удаление лайка фильму с id: {}, от пользователя id: {}", id, userId);
        validator.validateId(id);
        validator.validateId(userId);
        service.removeLike(id, userId);

    }

}
