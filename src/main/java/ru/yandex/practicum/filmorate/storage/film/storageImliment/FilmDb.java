package ru.yandex.practicum.filmorate.storage.film.storageImliment;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.dao.LikeDao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

@Component
@Primary
public class FilmDb implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final LikeDao likesDao;


    public FilmDb(JdbcTemplate jdbcTemplate, LikeDao likesDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.likesDao = likesDao;
    }


    @Override
    public Film findFilm(int id) {
        if (!checkFilmId(id)) {
            throw new NotFoundException("Фильм с идентификатором " + id + " не найден!");
        }
        String sqlQuery = "SELECT f.*, " +
                "m.rating AS mpa_name, " +
                "m.description AS mpa_description, " +
                "m.rating_id AS mpa_id, " +
                "FROM films AS f " +
                "JOIN mpa_ratings AS m ON f.mpa_id = m.rating_id " +
                "WHERE film_id = ?";

        Film film = jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, id);
        film.setGenres(getGenresOfFilm(id));
        film.setDirectors(getDirectorOfFilm(id));
        film.setLikes(likesDao.getFilmLikes(id));
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        String sqlQuery = "SELECT f.*, " +
                "m.rating AS mpa_name, " +
                "m.description AS mpa_description, " +
                "m.rating_id AS mpa_id " +
                "FROM films AS f " +
                "JOIN mpa_ratings AS m ON f.mpa_id = m.rating_id ";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm);
        getFilmGenres(films);
        getFilmLikes(films);
        getFilmDirector(films);
        return films;
    }

    @Override
    public void deleteFilm(int id) {
        String deleteLikesQuery = "DELETE FROM films_likes WHERE film_id = ?";
        jdbcTemplate.update(deleteLikesQuery, id);

        String deleteGenresQuery = "DELETE FROM films_genres WHERE film_id = ?";
        jdbcTemplate.update(deleteGenresQuery, id);

        String deleteDirectorQuery = "DELETE FROM films_director WHERE film_id = ?";
        jdbcTemplate.update(deleteDirectorQuery, id);

        String deleteFilmQuery = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(deleteFilmQuery, id);
    }

    private void getFilmGenres(List<Film> films) {
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));

        final Map<Integer, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));

        final String sqlQuery = "SELECT * " +
                "FROM genres AS g " +
                "INNER JOIN films_genres AS fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id IN(" + inSql + ")";

        jdbcTemplate.query(sqlQuery, (rs) -> {
            final Film film = filmById.get(rs.getInt("film_id"));
            film.getGenres().add(makeGenre(rs, films.size()));
        }, films.stream().map(Film::getId).toArray());
    }

    private void getFilmLikes(List<Film> films) {
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));

        final Map<Integer, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));

        final String sqlQuery = "SELECT film_id, user_id FROM films_likes WHERE film_id IN(" + inSql + ")";

        jdbcTemplate.query(sqlQuery, (rs) -> {
            final int filmId = rs.getInt("film_id");
            final int userId = rs.getInt("user_id");
            final Film film = filmById.get(filmId);
            if (film != null) {
                film.getLikes().add(userId);
            }
        }, films.stream().map(Film::getId).toArray());
    }

    private void getFilmDirector(List<Film> films) {
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));

        final Map<Integer, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        final String sqlQuery = "SELECT * FROM directors AS d " +
                "INNER JOIN films_director AS fd ON d.director_id = fd.director_id " +
                "WHERE fd.film_id IN (" + inSql + ")";
        jdbcTemplate.query(sqlQuery, (rs) -> {
            final Film film = filmById.get(rs.getInt("film_id"));
            film.getDirectors().add(makeDirector(rs, filmById.size()));
        }, films.stream().map(Film::getId).toArray());

    }

    @Override
    public Film createFilm(Film film) {

        try {
            String sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_id)"
                    + "VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                stmt.setInt(4, film.getDuration());
                stmt.setInt(5, film.getMpa().getId());
                return stmt;
            }, keyHolder);

            film.setId(keyHolder.getKey().intValue());
            if (film.getGenres() != null) {
                addGenresToFilm(film);
            }
            if (film.getDirectors() != null) {
                addDirectorToFilm(film);
            }

            return film;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create film: " + e.getMessage(), e);
        }
    }

    @Override
    public Film updateFilm(Film film) {
        if (!checkFilmId(film.getId())) {
            throw new NotFoundException("Фильм с идентификатором " + film.getId() + " не найден!");
        }
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            deleteGenresFromFilm(film);
            film.setGenres(new LinkedHashSet<>());
        } else {
            film.setGenres(new LinkedHashSet<>(film.getGenres()));
            updateGenresOfFilm(film);
        }
        if (film.getDirectors() == null || film.getDirectors().isEmpty()) {
            deleteDirectorFromFilm(film);
            film.setDirectors(new LinkedHashSet<>());
        } else {
            film.setDirectors(new LinkedHashSet<>(film.getDirectors()));
            updateDirectorOfFilm(film);
        }
        return film;
    }


    @Override
    public Collection<Film> findPopularFilms(Integer count, Integer genreId, Integer year) {
        String sqlQuery = "SELECT f.*, " +
                "m.rating AS mpa_name, " +
                "m.description AS mpa_description, " +
                "m.rating_id AS mpa_id, " +
                "COUNT(l.user_id) AS like_count " +
                "FROM films AS f " +
                "JOIN mpa_ratings AS m ON f.mpa_id = m.rating_id " +
                "LEFT JOIN films_likes AS l ON l.film_id = f.film_id ";

        if (genreId != null) {
            if (checkGenreId(genreId)) {
                sqlQuery += "LEFT JOIN films_genres AS fg ON f.film_id = fg.film_id ";
            } else {
                throw new NotFoundException("Жанр с идентификатором " + genreId + " не найден!");
            }
        }
        if (year != null) {
            sqlQuery += "WHERE YEAR(f.release_date) = ? ";
        } else {
            sqlQuery += "WHERE 1=1 ";
        }
        if (genreId != null) {
            sqlQuery += "AND fg.genre_id = ? ";
        }

        sqlQuery += "GROUP BY f.film_id, f.name " +
                "ORDER BY like_count DESC, f.film_id ASC ";

        if (count != null) {
            sqlQuery += "LIMIT ? ";
        }

        List<Object> params = new ArrayList<>();
        if (year != null) {
            params.add(year);
        }
        if (genreId != null) {
            params.add(genreId);
        }
        if (count != null) {
            params.add(count);
        }

        List<Film> films = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs, rowNum), params.toArray());
        getFilmGenres(films);
        getFilmLikes(films);
        getFilmDirector(films);
        return films;
    }


    @Override
    public Collection<Film> findListOfCommonFilms(int userId, int friendId) {
        if (!checkUserId(userId)) {
            throw new NotFoundException("Фильм с идентификатором " + userId + " не найден!");
        } else if (!checkUserId(friendId)) {
            throw new NotFoundException("Фильм с идентификатором " + friendId + " не найден!");
        }
        String sqlQuery = "SELECT f.*, " +
                "m.rating AS mpa_name, " +
                "m.rating_id AS mpa_id, " +
                "m.description AS mpa_description, " +
                "FROM films AS f " +
                "JOIN mpa_ratings AS m ON f.mpa_id = m.rating_id " +
                "LEFT JOIN films_likes AS l ON l.film_id = f.film_id " +
                "WHERE f.film_id IN (SELECT l.film_id FROM films_likes AS l WHERE user_id = ? AND " +
                "film_id IN (SELECT l.film_id FROM films_likes AS l WHERE user_id = ?)) " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm, userId, friendId);
        getFilmGenres(films);
        getFilmLikes(films);
        getFilmDirector(films);
        return films;
    }

    @Override
    public Collection<Film> findAllPopularFilms(String query, String by) {
        if (query == null & by == null || query.isBlank() & by.isBlank()) {
            return getAllFilmSortedByPopular();
        } else if (query != null & by != null || !query.isBlank() & !by.isBlank()) {
            String[] byAll = by.split(",");
            if (byAll.length == 1) {
                if (byAll[0].equals("title")) {
                    String query1 = query.substring(0).toLowerCase();
                    String query2 = query.substring(0, 1).toUpperCase() + query.substring(1).toLowerCase();
                    return getAllFilmSortedByPopular().stream()
                            .filter(film -> film.getName().contains(query1) || film.getName().contains(query2))
                            .collect(Collectors.toList());
                } else if (byAll[0].equals("director")) {
                    String query1 = query.substring(0, 1).toLowerCase() + query.substring(1).toLowerCase();
                    String query2 = query.substring(0, 1).toUpperCase() + query.substring(1).toLowerCase();
                    return getAllFilmsSortedByNameDirector(query1, query2);
                } else {
                    throw new ValidationException("некорректный запрос в by" + byAll[0]);
                }
            } else if (byAll.length == 2) {
                String query1 = query.substring(0, 1).toLowerCase() + query.substring(1).toLowerCase();
                String query2 = query.substring(0, 1).toUpperCase() + query.substring(1).toLowerCase();
                Collection<Film> filmsByDirector = getAllFilmsSortedByNameDirector(query1, query2);
                Collection<Film> filmsByTitle = getAllFilmSortedByPopular().stream()
                        .filter(film -> film.getName().contains(query1) || film.getName().contains(query2))
                        .collect(Collectors.toList());
                filmsByDirector.addAll(filmsByTitle);
                return filmsByDirector;
            } else {
                throw new ValidationException("Некорректный запрос");
            }
        } else {
            throw new ValidationException("Некорректный запрос, запрос должен состоять из имя режиссера и названия");
        }
    }

    private Collection<Film> getAllFilmSortedByPopular() {
        String sqlQuery = "SELECT f.*, " +
                "m.rating AS mpa_name, " +
                "m.rating_id AS mpa_id, " +
                "m.description AS mpa_description, " +
                "FROM films AS f " +
                "JOIN mpa_ratings AS m ON f.mpa_id = m.rating_id " +
                "LEFT JOIN films_likes AS l ON l.film_id = f.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm);
        getFilmGenres(films);
        getFilmLikes(films);
        getFilmDirector(films);
        return films;
    }

    private Collection<Film> getAllFilmsSortedByNameDirector(String nameDirector1, String nameDirector2) {
        String sqlQuery = "SELECT f.FILM_ID ,f.NAME ,f.DESCRIPTION , f.RELEASE_DATE , f.DURATION , " +
                "m.rating as mpa_name, " +
                "m.rating_id as mpa_id, " +
                "m.description as mpa_description, " +
                "FROM FILMS_DIRECTOR AS fd " +
                "JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "JOIN FILMS AS f ON fd.FILM_ID = f.FILM_ID " +
                "JOIN mpa_ratings as m ON f.mpa_id = m.rating_id " +
                "WHERE d.DIRECTOR_NAME LIKE '%" + nameDirector1 + "%' OR d.DIRECTOR_NAME LIKE '%" + nameDirector2 + "%'";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm);
        getFilmGenres(films);
        getFilmLikes(films);
        getFilmDirector(films);
        return films;
    }

    public void deleteGenresFromFilm(Film film) {
        String sqlQuery = "DELETE FROM films_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    public void addGenresToFilm(Film film) {
        for (Genre genre : film.getGenres()) {
            String setNewGenres = "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.update(setNewGenres, film.getId(), genre.getId());
        }
    }

    public void updateGenresOfFilm(Film film) {
        String sqlQuery = "DELETE FROM films_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        addGenresToFilm(film);
    }

    public Collection<Genre> getGenresOfFilm(int filmId) {
        String sqlQuery = "SELECT * FROM genres " +
                "INNER JOIN films_genres AS fg ON genres.genre_id = fg.genre_id " +
                "WHERE film_id = ?";
        return jdbcTemplate.query(sqlQuery, this::makeGenre, filmId);
    }

    private Film makeFilm(ResultSet resultSet, int i) throws SQLException {
        return new Film(
                resultSet.getInt("film_id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getDate("release_date").toLocalDate(),
                resultSet.getInt("duration"),
                new HashSet<Integer>(),
                new ArrayList<Genre>(),
                new ArrayList<Director>(),
                new Mpa(resultSet.getInt("mpa_id"),
                        resultSet.getString("mpa_name"),
                        resultSet.getString("mpa_description")),
                new HashSet<Mark>()
        );

    }

    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("genre_id"),
                resultSet.getString("genre_name"));
    }

    private boolean checkFilmId(int id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM films WHERE film_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }

    private boolean checkGenreId(int id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM genres WHERE genre_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }

    private boolean checkUserId(int id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM users WHERE user_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }

    public void addDirectorToFilm(Film film) {
        for (Director director : film.getDirectors()) {
            String sqlQuery = "INSERT INTO films_director (film_id, director_id) VALUES (?,?)";
            jdbcTemplate.update(sqlQuery, film.getId(), director.getId());
        }
    }

    public void deleteDirectorFromFilm(Film film) {
        String sqlQuery = "DELETE FROM films_director WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    public void updateDirectorOfFilm(Film film) {
        deleteDirectorFromFilm(film);
        addDirectorToFilm(film);
    }

    private Director makeDirector(ResultSet rs, int RowNum) throws SQLException {
        return new Director(rs.getInt("director_id"),
                rs.getString("director_name"));
    }

    public Collection<Director> getDirectorOfFilm(int filmId) {
        String sqlQuery = "SELECT * FROM directors " +
                "INNER JOIN films_director AS fd ON directors.director_id = fd.director_id " +
                "WHERE film_id = ?";
        return jdbcTemplate.query(sqlQuery, this::makeDirector, filmId);
    }

    public Optional<Film> findFilmById(int filmId) {
        String sqlQuery = "select * from films where film_id = ?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, filmId));
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException(String.format("Фильм № %d не найден", filmId));
        }
    }

    @Override
    public List<Film> getRecommendations(int id) {
        if (checkUserId(id)) {
            String sqlQuery = "SELECT f.*, m.rating as mpa_name, m.description as mpa_description, m.rating_id as mpa_id " +
                    "FROM films as f JOIN mpa_ratings as m ON f.mpa_id = m.rating_id " +
                    "WHERE film_id IN (SELECT film_id FROM films_likes " +
                    "WHERE user_id IN (SELECT user_id FROM films_likes " +
                    "WHERE film_id IN (SELECT film_id FROM films_likes WHERE user_id = ?) AND user_id <> ? " +
                    "GROUP BY user_id ORDER BY COUNT(user_id) DESC) AND " +
                    "film_id NOT IN (SELECT film_id FROM films_likes WHERE user_id = ?))";
            List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm, id, id, id);
            getFilmGenres(films);
            getFilmLikes(films);
            getFilmDirector(films);
            return films;
        } else {
            throw new NotFoundException("Пользователь с идентификатором " + id + " не найден!");
        }
    }

}