package ru.yandex.practicum.filmorate.storage.dao.film;

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
class FilmDaoStorageTest {
    private final FilmDaoStorage filmDaoStorage;
    private final MpaRatingDaoImpl mpaRatingDao;

    @Test
    void listFilms() {
        assertEquals(4, filmDaoStorage.allFilms().size());
    }

    @Test
    void getFilmById() {
        Optional<Film> filmOptional = Optional.ofNullable(filmDaoStorage.getFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
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
        filmDaoStorage.createFilm(film);
        List<Film> films = filmDaoStorage.allFilms();
        assertEquals(5, films.size());
    }

    @Test
    void updateFilm() {
        Film film = new Film();
        film.setId(1);
        film.setName("filmUpdated");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.ofEpochDay(1985 - 5 - 5));
        film.setDuration(105);
        film.setRate(5);
        film.setMpa(mpaRatingDao.getMpaRatingById(1));
        filmDaoStorage.updateFilm(film);
        String value = filmDaoStorage.getFilmById(1).getName();
        assertEquals("filmUpdated", value);
    }

    @Test
    void addLikePlusGetMostPopularFilms() {
        filmDaoStorage.addLike(1, 1);
        Film popularFilm = filmDaoStorage.getMostPopularFilms(1).get(0);
        assertEquals(filmDaoStorage.getFilmById(1), popularFilm);
    }

    @Test
    void deleteLike() {
        filmDaoStorage.addLike(1, 1);
        filmDaoStorage.addLike(1, 2);
        filmDaoStorage.addLike(2, 1);
        filmDaoStorage.addLike(2, 2);
        filmDaoStorage.addLike(2, 3);
        filmDaoStorage.deleteLike(2, 1);
        filmDaoStorage.deleteLike(2, 3);
        Film popularFilm = filmDaoStorage.getMostPopularFilms(2).get(1);
        assertEquals(filmDaoStorage.getFilmById(2), popularFilm);
    }
}