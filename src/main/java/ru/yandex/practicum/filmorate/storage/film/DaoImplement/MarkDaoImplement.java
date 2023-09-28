package ru.yandex.practicum.filmorate.storage.film.DaoImplement;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mark;
import ru.yandex.practicum.filmorate.storage.film.dao.MarkDao;

import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@Primary
@RequiredArgsConstructor
public class MarkDaoImplement implements MarkDao {

    private final JdbcTemplate jdbcTemplate;

    public List<Integer> findLikes(Film film) {
        String sqlQuery = "SELECT user_id FROM marks WHERE film_id = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getInt("user_id"), film.getId());
    }

    public List<Mark> findMarks(Film film) {
        String sqlQuery = "SELECT * FROM marks WHERE film_id = ?";
        return jdbcTemplate.query(sqlQuery, this::makeMark, film.getId());
    }

    public Map<Integer, Set<Integer>> findAllUsersWithPositiveMarks() {
        Map<Integer, Set<Integer>> usersWithMarks = new HashMap<>();
        String sqlQueryUsersId = "SELECT user_id FROM marks WHERE mark > 5 GROUP BY user_id ";
        List<Integer> users = jdbcTemplate.query(sqlQueryUsersId, (rs, rowNum) -> rs.getInt("user_id"));
        for (Integer user : users) {
            String sqlQueryFilmsId = "SELECT film_id FROM marks WHERE user_id = ? AND mark > 5 ";
            List<Integer> marks = jdbcTemplate.query(sqlQueryFilmsId,
                    (rs, rowNum) -> rs.getInt("film_id"), user);
            usersWithMarks.put(user, new HashSet<>(marks));
        }
        return usersWithMarks;
    }

    private Mark makeMark(ResultSet rs, int rowNum) throws SQLException {
        return new Mark(rs.getInt("user_id"), rs.getInt("mark"));
    }

}
