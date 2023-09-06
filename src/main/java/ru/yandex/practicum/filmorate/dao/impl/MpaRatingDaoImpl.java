package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaRatingDao;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

@AllArgsConstructor
@Component
public class MpaRatingDaoImpl implements MpaRatingDao {
    private static final String GET_RATINGS = "SELECT * FROM rating";
    private static final String GET_RATING_BY_ID = "SELECT * FROM rating WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<MpaRating> getMpaRatings() {
        return jdbcTemplate.query(GET_RATINGS, new BeanPropertyRowMapper<>(MpaRating.class));
    }

    @Override
    public MpaRating getMpaRatingById(int id) {
        return jdbcTemplate.queryForObject(GET_RATING_BY_ID, new BeanPropertyRowMapper<>(MpaRating.class), id);
    }
}
