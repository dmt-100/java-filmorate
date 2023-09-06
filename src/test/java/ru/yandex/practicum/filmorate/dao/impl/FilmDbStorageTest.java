package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final MpaRatingDaoImpl mpaRatingDao;

    @Test
    void listFilms() {
        assertEquals(4, filmDbStorage.listFilms().size());
    }

    @Test
    void getFilmById() {
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.getFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("filmId", 1)
                );
    }

    @Test
    void createFilm() {
        Film film = new Film();
        film.setName("film5");
        film.setDescription("description5");
        film.setReleaseDate(LocalDate.ofEpochDay(1985 - 5 - 5));
        film.setDuration(105);
        film.setRate(5);
        film.setMpa(mpaRatingDao.getMpaRatingById(1));
        filmDbStorage.createFilm(film);
        List<Film> films = filmDbStorage.listFilms();
        assertEquals(5, films.size());
    }

    @Test
    void updateFilm() {
        Film film = new Film();
        film.setFilmId(1);
        film.setName("filmUpdated");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.ofEpochDay(1985 - 5 - 5));
        film.setDuration(105);
        film.setRate(5);
        film.setMpa(mpaRatingDao.getMpaRatingById(1));
        filmDbStorage.updateFilm(film);
        String value = filmDbStorage.getFilmById(1).getName();
        assertEquals("filmUpdated", value);
    }

    @Test
    void addLikePlusGetMostPopularFilms() {
        filmDbStorage.addLike(1, 1);
        Film popularFilm = filmDbStorage.getMostPopularFilms(1).get(0);
        assertEquals(filmDbStorage.getFilmById(1), popularFilm);
    }

    @Test
    void deleteLike() {
        filmDbStorage.addLike(1, 1);
        filmDbStorage.addLike(1, 2);
        filmDbStorage.addLike(2, 1);
        filmDbStorage.addLike(2, 2);
        filmDbStorage.addLike(2, 3);
        filmDbStorage.deleteLike(2, 1);
        filmDbStorage.deleteLike(2, 3);
        Film popularFilm = filmDbStorage.getMostPopularFilms(2).get(1);
        assertEquals(filmDbStorage.getFilmById(2), popularFilm);
    }
}