package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) {
        filmService.createFilm(film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film filmToUpdate) {
        filmService.updateFilm(filmToUpdate);
        return filmToUpdate;

    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable int id) {
        filmService.deleteFilm(id);
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findFilm(@PathVariable int id) {
        return filmService.findFilm(id);

    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") int filmId, @PathVariable("userId") int userId) {
        filmService.addLike(filmId, userId);
        log.info("Пользователь c id {} поставил лайк фильму с id {}", userId, filmId);
    }


    @GetMapping("/{id}/likes")
    public int getAllLikes(@PathVariable int id) {
        return filmService.getAllLikes(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) Integer count,
            @RequestParam(value = "genreId", required = false) Integer genreId,
            @RequestParam(value = "year", required = false) Integer year) {
        log.info("Получен запрос на вывод {} популярных фильмов", count);
        return filmService.getPopularFilms(count, genreId, year);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        filmService.deleteLike(filmId, userId);
        log.info("Пользователь c id {} удалил лайка с фильма с id {}", userId, filmId);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getAllFilmsSortByDirector(@PathVariable("directorId") Integer directorId,
                                                      @RequestParam String sortBy) {
        log.info("Sort by " + sortBy + " directors by id " + directorId);
        return filmService.getSortFilmByDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    public Collection<Film> getAllPopularFilms(@RequestParam(required = false) String query,
                                               @RequestParam(required = false) String by) {
        log.info("Получен запрос search на вывод фильмов по популярности");
        return filmService.getAllPopularFilms(query, by);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam int userId,
                                           @RequestParam int friendId) {
        log.info("Пользователь с id " + userId + " запросил список общих фильмов с пользователем с id " + friendId);
        return filmService.getListOfCommonFilms(userId, friendId);
    }
}