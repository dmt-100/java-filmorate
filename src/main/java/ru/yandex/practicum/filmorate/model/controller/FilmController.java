package ru.yandex.practicum.filmorate.model.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.repository.FilmRepository;
import ru.yandex.practicum.filmorate.model.service.FilmValidator;
import ru.yandex.practicum.filmorate.model.service.IdCounter;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    FilmRepository filmRepository = new FilmRepository();
    FilmValidator filmValidator = new FilmValidator();

    @GetMapping
    public Set<Film> getFilms() { // получение всех фильмов.
        return filmRepository.getFilms();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) { // добавление фильма

        log.info("Пришел запрос на добавление фильма {}", film);

        film.setId(IdCounter.increaseFilmId());

        if (getFilms().contains(film)) {
            throw new ValidationException("Фильм " + film + ", уже есть в коллекции.");
        }
        if (filmValidator.validate(filmRepository, film)) {

            getFilms().add(film);
            log.info("Добавление фильма {}, количество фильмов: {}", film, getFilms().size());
        }
        return film;
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) {

        Film result = null;

        if (filmValidator.validate(filmRepository, film)) {

            for (Film f : filmRepository.getFilms()) {
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


}
