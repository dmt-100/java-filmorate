package ru.yandex.practicum.filmorate.storage.dao.film;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class LikeDaoImpl implements LikeDao {

    private static final String ADD_LIKE = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLikeToFilm(long id, long userId) {
        jdbcTemplate.update(ADD_LIKE, id, userId);
    }

    @Override
    public void deleteLikeFromFilm(long id, long userId) {
        jdbcTemplate.update(DELETE_LIKE, id, userId);
    }
}
