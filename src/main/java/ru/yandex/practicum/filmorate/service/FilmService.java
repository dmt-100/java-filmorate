package ru.yandex.practicum.filmorate.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmDaoStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Service
public class FilmService implements FilmServiceImpl {
    private final FilmDaoStorage filmDaoStorage;
    private final Validator validator;

    public FilmService(@Qualifier("filmDaoStorage") FilmDaoStorage filmDaoStorage, Validator validator) {
        this.filmDaoStorage = filmDaoStorage;
        this.validator = validator;
    }

    public void addLike(int id, int userId) {
        filmDaoStorage.addLike(id, userId);
    }

    public void deleteLike(int id, int userId) {
        filmDaoStorage.deleteLike(id, userId);
    }

    @Override
    public Film createFilm(@Valid Film film) {
        validator.validateFilm(film);
        filmDaoStorage.createFilm(film);
        log.debug("Сохранен фильм: {}", film);
        return film;
    }

    public List<Film> getMostPopularFilms(int count) {
        if (count > 0) {
            return filmDaoStorage.getMostPopularFilms(count);
        } else {
            log.warn("Ошибка запроса списка популярных фильмов.");
            throw new ValidationException("Ошибка запроса списка популярных фильмов, проверьте корректность данных.");
        }
    }

    @Override
    public List<Film> allFilms() {
        return filmDaoStorage.allFilms();
    }

    @Override
    public Film getFilmById(int id) {
        try {
            return filmDaoStorage.getFilmById(id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Ошибка запроса фильма.");
            throw new ResourceNotFoundException("Ошибка запроса фильма, проверьте корректность данных.");
        }
    }

    @Override
    public Film updateFilm(@NonNull Film film) {
        try {
            validator.validateFilm(film);
            validator.validateFilmId(filmDaoStorage.allFilms().size(), film.getId());
            int filmIdInStorage = getFilmById(film.getId()).getId();
            int filmId = film.getId();
            if (filmIdInStorage == filmId) {
                filmDaoStorage.updateFilm(film);
                log.debug("Обновлен фильм: {}", film);
            }
        } catch (ResourceNotFoundException e) {
            log.warn("Ошибка при обновлении фильма: {}", film);
            throw new ResourceNotFoundException("Ошибка при изменении фильма, проверьте корректность данных.");
        }
        return film;
    }

    @Override
    public void deleteFilm(int id) {
        if (id > 0) {
            filmDaoStorage.deleteFilm(id);
            log.warn("Фильм удалён.");
        } else {
            log.warn("Ошибка при удалении фильма с id: {}", filmDaoStorage.getFilmById(id));
            throw new ResourceNotFoundException("Ошибка при удалении фильма, проверьте корректность id фильма.");
        }
    }

}
