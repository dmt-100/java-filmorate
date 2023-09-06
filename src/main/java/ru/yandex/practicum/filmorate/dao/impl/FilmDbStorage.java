package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.LikeDao;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.ValidationException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Component
public class FilmDbStorage implements FilmStorage {
    private static final String GET_ALL_FILMS = "SELECT * FROM films f LEFT JOIN rating r ON r.id = f.rating_id";
    private static final String GET_FILM_BY_ID = "SELECT * FROM films f LEFT JOIN rating r ON r.id = f.rating_id WHERE film_id = ?";
    private static final String CREATE_FILM = "INSERT INTO films " +
            "(name, description, release_date, duration, rating_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_FILM_GENRES = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String UPDATE_FILM = "UPDATE films " +
            "SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
            "WHERE film_id = ?";
    private static final String GET_MOST_POPULAR_FILMS = "SELECT * FROM films f " +
            "LEFT JOIN (SELECT film_id, COUNT(*) likes_count FROM likes GROUP BY film_id) l ON f.film_id = l.film_id " +
            "LEFT JOIN rating r ON r.id = f.rating_id " +
            "ORDER BY l.likes_count DESC LIMIT ?";
    private static final String GET_FILM_GENRES = "SELECT f.id, name FROM film_genres f" +
            " LEFT JOIN (SELECT * FROM genres) g ON f.id = g.id " +
            "WHERE film_id = ?";
    private static final String ADD_GENRE_TO_FILM = "INSERT INTO film_genres (film_id, id) VALUES (?, ?)";

    private final JdbcTemplate jdbcTemplate;

    private final UserService userService;
    private final LikeDao likeDao;

    @Override
    public Film createFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(CREATE_FILM, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            film.setId((Integer) keyHolder.getKey());
        } else {
            throw new ValidationException("Ошибка генерации id в базе.");
        }
        return addGenreToFilm(film);
    }

    @Override
    public List<Film> allFilms() {
        return jdbcTemplate.query(GET_ALL_FILMS, this::mapRowToFilm);
    }

    @Override
    public Film getFilmById(int id) {
        return jdbcTemplate.queryForObject(GET_FILM_BY_ID, this::mapRowToFilm, id);
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update(DELETE_FILM_GENRES, film.getId());
        jdbcTemplate.update(UPDATE_FILM, film.getName(), film.getDescription(),
                Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getMpa().getId(), film.getId());
        return addGenreToFilm(film);
    }

    @Override
    public void deleteLike(int id, int userId) {
        if (getFilmById(id).getId() == id && userService.getUserById(userId).getId() == userId) {
            likeDao.deleteLikeFromFilm(id, userId);
        } else {
            log.warn("Ошибка при добавлении лайка фильму.");
            throw new ResourceNotFoundException("Ошибка добавления лайка, проверьте корректность данных.");
        }
    }


    @Override
    public void addLike(int id, int userId) {
        if (getFilmById(id).getId() == id && userService.getUserById(userId).getId() == userId) {
            likeDao.addLikeToFilm(id, userId);
        } else {
            log.warn("Ошибка при добавлении лайка фильму.");
            throw new ResourceNotFoundException("Ошибка добавления лайка, проверьте корректность данных.");
        }
    }


    @Override
    public List<Film> getMostPopularFilms(int count) {
        return jdbcTemplate.query(GET_MOST_POPULAR_FILMS, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) {
        try {
            MpaRating mpa = new MpaRating();
            Film film = new Film();
            film.setId(resultSet.getInt("film_id"));
            film.setName(resultSet.getString("name"));
            film.setDescription(resultSet.getString("description"));
            film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
            film.setDuration(resultSet.getInt("duration"));
            mpa.setId(resultSet.getInt("id"));
            mpa.setTitle(resultSet.getString("title"));
            film.setMpa(mpa);
            film.setGenres(getFilmGenres(film.getId()));
            return film;
        } catch (EmptyResultDataAccessException | SQLException e) {
            throw new ResourceNotFoundException("Ошибка запроса, проверьте корректность данных.");
        }
    }

    private List<Genre> getFilmGenres(int id) {
        return jdbcTemplate.query(GET_FILM_GENRES, new BeanPropertyRowMapper<>(Genre.class), id);
    }

    private Film addGenreToFilm(Film film) {
        List<Genre> uniqueGenre = film.getGenres()
                .stream()
                .distinct()
                .collect(Collectors.toList());
        uniqueGenre.forEach(x -> jdbcTemplate.update(ADD_GENRE_TO_FILM, film.getId(), x.getId()));
        film.setGenres(uniqueGenre);
        return film;
    }
}
