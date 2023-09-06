package ru.yandex.practicum.filmorate.model.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.service.FilmService;

import java.util.*;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;

    public FilmController(FilmService service) {
        this.service = service;
    }


    @GetMapping
    public Set<Film> getFilms() {
        return service.getFilms();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Film getFilmById(@PathVariable int id) {
        log.info("Получение фильма по id: {}", id);
        return service.getFilmById(id);
    }

    @GetMapping("/popular")
    public Set<Film> getPopularFilms(@RequestParam(value = "count", required = false) @PathVariable Integer count) {
        log.info("Получение наиболее популярных фильмов: {}", count);
        return service.getMostPopularFilms(Objects.requireNonNullElse(count, 10));
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.info("Добавление фильма {}", film);
        return service.createFilm(film);
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) {
        log.info("Обновление фильма {}", film);
        return service.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLikeToFilm(@PathVariable int id, @PathVariable int userId) {
        log.info("Добавление лайка фильму с id: {}, от пользователя id: {}", id, userId);
        return service.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Удаление лайка фильму с id: {}, от пользователя id: {}", id, userId);
        service.removeLike(id, userId);
    }
}
