package ru.yandex.practicum.filmorate.model.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.service.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.service.IdCounter;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    private Film film;
    private Film film2;
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();
    }

    @AfterEach
    void tearDown() {
        filmController = new FilmController();
        filmController.getFilms().remove(film);
        IdCounter.setIdFilmCounter(0);
    }

    @Test
    void testCreateFilmWithEmptyName() {
        film.setName("");
        filmController = new FilmController();
        Throwable exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(film));
        assertEquals("Название фильма не должно быть пустым, " + film.getName().isBlank(),
                exception.getMessage());
    }

    @Test
    void testCreateFilmWithTooLongDescription() {
        filmController = new FilmController();
        final String DESCRIPTION_MORE_THAN_200 = "Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                "Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
                "а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.";
        film.setDescription(DESCRIPTION_MORE_THAN_200);
        Throwable exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(film));
        assertEquals("Максимальное количество букв в описании фильма не должно превышать 200, description.length(): "
                + DESCRIPTION_MORE_THAN_200.length(), exception.getMessage());
    }

    @Test
    void testCreateFilmWithWrongReleaseDate() {
        filmController = new FilmController();
        film.setReleaseDate(LocalDate.of(1890, 3, 25));

        Throwable exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(film));
        assertEquals("Некорректная дата фильма. Дата: " + film.getReleaseDate(), exception.getMessage());
    }

    @Test
    void testCreateFilmWithWrongDuration() {
        filmController = new FilmController();
        film.setDuration(-200);

        Throwable exception = assertThrows(ValidationException.class,
                () -> filmController.createFilm(film));

        assertEquals("Продолжительность фильма должна быть положительной: " + film.getDuration(),
                exception.getMessage());
    }

    @Test
    void testPutFilmUpdate() {
        filmController = new FilmController();
        filmController.createFilm(film);

        film2 = Film.builder()
                .id(1)
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .rate(4)
                .build();
        filmController.putFilm(film2);
        for (Film filmRepositoryFilm : filmController.getStorage().getFilms()) {
            assertEquals(1, filmController.getStorage().getFilms().size());
            assertEquals(filmRepositoryFilm.toString(), film2.toString());
        }
    }

    @Test
    void testPutFilmUpdateWithWrongId() {
        filmController = new FilmController();
        filmController.createFilm(film);

        film2 = Film.builder()
                .id(9999)
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .rate(4)
                .build();
        Throwable exception = assertThrows(ValidationException.class,
                () -> filmController.putFilm(film2));
        assertEquals("Некорректный id фильма. Id: " + film2.getId(), exception.getMessage());
    }

    @Test
    void testGetFilms() {
        filmController = new FilmController();
        filmController.createFilm(film);

        film2 = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .rate(4)
                .build();
        filmController.createFilm(film2);
        assertEquals(filmController.getStorage().getFilms(), filmController.getFilms());
        assertEquals(2, filmController.getFilms().size());
    }
}

