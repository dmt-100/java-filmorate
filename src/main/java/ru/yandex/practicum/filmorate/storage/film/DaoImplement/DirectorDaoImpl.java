package ru.yandex.practicum.filmorate.storage.film.DaoImplement;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.dao.DirectorDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class DirectorDaoImpl implements DirectorDao {
    private final JdbcTemplate jdbcTemplate;

    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Director> getAllDirectors() {
        String sqlQuery = "SELECT * FROM directors";
        return jdbcTemplate.query(sqlQuery, this::makeDirector);
    }

    @Override
    public Director getDirectorById(Integer directorId) {
        String sqlQuery = "SELECT * FROM directors WHERE director_id = ?";
        Director director;
        try {
            director = jdbcTemplate.queryForObject(sqlQuery, this::makeDirector, directorId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Режиссер с идентификатором " + directorId +
                    " не найден!");
        }
        return director;
    }

    @Override
    public void deleteDirectorById(Integer directorId) {
        getDirectorById(directorId);
        String sqlQuery = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(sqlQuery, directorId);
    }

    @Override
    public Director createDirector(Director director) {
        try {
            if (!checkNameDirector(director.getName())) {
                throw new ValidationException("Имя режиссера не может быть пустым");
            } else {
                String sqlQuery = "INSERT INTO directors (director_name) VALUES (?)";
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection -> {
                    PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"director_id"});
                    stmt.setString(1, director.getName());
                    return stmt;
                }, keyHolder);
                director.setId(keyHolder.getKey().intValue());
                return director;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create director: " + e.getMessage(), e);
        }
    }

    @Override
    public Director updateDirector(Director director) {
        getDirectorById(director.getId());
        if (!checkNameDirector(director.getName())) {
            throw new ValidationException("Имя режиссера не может быть пустым");
        } else {
            String sqlQuery = "UPDATE directors SET director_name = ? WHERE director_id = ?";
            jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
            return director;
        }
    }

    private Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        return new Director(rs.getInt("director_id"),
                rs.getString("director_name"));
    }

    private boolean checkNameDirector(String nameDirector) {
        if (nameDirector.isBlank()) {
            return false;
        } else {
            return true;
        }
    }
}
