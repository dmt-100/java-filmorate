package ru.yandex.practicum.filmorate.storage.memory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmIdCounter;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FilmControllerTest {
    private Film film;
    private Film film2;
    @Autowired
    private InMemoryFilmStorage inMemoryFilmStorage;
    private FilmIdCounter filmIdCounter;

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setId(1);
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(100);
    }

    @AfterEach
    void tearDown() {
        filmIdCounter = new FilmIdCounter();
        inMemoryFilmStorage.getFilms().clear();
        filmIdCounter.setIdFilmCounter(0);
    }

    @Test
    void testCreateFilmWithEmptyName() {
        film.setName("");
        Throwable exception = assertThrows(ValidationException.class, () -> inMemoryFilmStorage.updateFilm(film));
        assertEquals("Название фильма не должно быть пустым.", exception.getMessage());
    }


    @Test
    void testCreateFilmWithTooLongDescription() {
        final int MAX_DESCRIPTION_LENGTH = 200;
        final String DESCRIPTION_MORE_THAN_200 = "Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " + "Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " + "а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.";
        film.setDescription(DESCRIPTION_MORE_THAN_200);
        Throwable exception = assertThrows(ValidationException.class, () -> inMemoryFilmStorage.updateFilm(film));
        assertEquals("Максимальное количество букв в описании фильма не должно превышать 200", exception.getMessage());
    }

    @Test
    void testCreateFilmWithWrongReleaseDate() {
        film.setReleaseDate(LocalDate.of(1890, 3, 25));
        Throwable exception = assertThrows(ValidationException.class, () -> inMemoryFilmStorage.createFilm(film));
        assertEquals("Некорректная дата фильма: " + film.getReleaseDate(), exception.getMessage());
    }

    @Test
    void testCreateFilmWithWrongDuration() {
        film.setDuration(-200);
        Throwable exception = assertThrows(ValidationException.class, () -> inMemoryFilmStorage.updateFilm(film));
        assertEquals("Продолжительность фильма должна быть положительной: " + film.getDuration(), exception.getMessage());
    }

    @Test
    void testPutFilmUpdateWithWrongId() {
        inMemoryFilmStorage.createFilm(film);
        film.setId(9999);
        Throwable exception = assertThrows(ResourceNotFoundException.class, () -> inMemoryFilmStorage.updateFilm(film));
        assertEquals("Некорректный идентификатор фильма.", exception.getMessage());
    }

    @Test
    void testGetFilms() {
        inMemoryFilmStorage.createFilm(film);
        Film film2 = new Film();
        film2.setName("nisi eiusmod");
        film2.setDescription("adipisicing");
        film2.setReleaseDate(LocalDate.of(1967, 3, 25));
        film2.setDuration(100);
        film2.setRate(4);
        inMemoryFilmStorage.createFilm(film2);
        assertEquals(inMemoryFilmStorage.getFilms(), inMemoryFilmStorage.getFilms());
        assertEquals(2, inMemoryFilmStorage.getFilms().size());
    }
}

