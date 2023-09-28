package ru.yandex.practicum.filmorate.storage.film.DaoImplement;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.feed.OperType;
import ru.yandex.practicum.filmorate.storage.feed.FeedSaveDao;
import ru.yandex.practicum.filmorate.storage.film.dao.ReviewDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Component
public class ReviewDaoImplement implements ReviewDao {
    private final JdbcTemplate jdbcTemplate;
    private final FeedSaveDao feedSaveDao;

    public ReviewDaoImplement(JdbcTemplate jdbcTemplate, FeedSaveDao feedSaveDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.feedSaveDao = feedSaveDao;
    }

    @Override
    public Review saveReview(Review review) {
        if (review.getIsPositive() == null) {
            throw new ValidationException("Поле 'isPositive' должно быть указано.");
        }


        userAndFilmCheck(review);
        review.setUseful(0);
        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"review_id"});
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setInt(3, review.getUserId());
            ps.setInt(4, review.getFilmId());
            ps.setInt(5, review.getUseful());

            return ps;
        }, keyHolder);

        int reviewId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        review.setReviewId(reviewId);
        feedSaveDao.saveEvent(review.getUserId(), feedSaveDao.getEventTypeId(EventType.REVIEW), feedSaveDao.getOperationTypeId(OperType.ADD), reviewId);
        return review;
    }


    @Override
    public void addLikeToReview(int reviewId, int userId) {
        String checkSql = "SELECT COUNT(*) FROM review_likes WHERE user_id = ? AND review_id = ?";
        int likeCount = Objects.requireNonNull(jdbcTemplate.queryForObject(checkSql, Integer.class, userId, reviewId));
        if (likeCount == 0) {
            String insertSql = "INSERT INTO review_likes (user_id, review_id) VALUES (?, ?)";
            jdbcTemplate.update(insertSql, userId, reviewId);

            String updateSql = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";
            jdbcTemplate.update(updateSql, reviewId);
        } else {
            throw new IllegalStateException("Пользователь уже поставил лайк к данному отзыву.");
        }
    }

    @Override
    public void deleteLikeToReview(int reviewId, int userId) {
        String checkSql = "SELECT COUNT(*) FROM review_likes WHERE user_id = ? AND review_id = ?";
        int likeCount = jdbcTemplate.queryForObject(checkSql, Integer.class, userId, reviewId);

        if (likeCount > 0) {
            String deleteSql = "DELETE FROM review_likes WHERE user_id = ? AND review_id = ?";
            jdbcTemplate.update(deleteSql, userId, reviewId);

            String updateSql = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";
            jdbcTemplate.update(updateSql, reviewId);
        } else {
            throw new IllegalStateException("У пользователя нет лайка к данному отзыву.");
        }
    }

    @Override
    public void addDislikeToReview(int reviewId, int userId) {
        String checkSql = "SELECT COUNT(*) FROM review_dislikes WHERE user_id = ? AND review_id = ?";
        int likeCount = Objects.requireNonNull(jdbcTemplate.queryForObject(checkSql, Integer.class, userId, reviewId));
        if (likeCount == 0) {
            String insertSql = "INSERT INTO review_dislikes (user_id, review_id) VALUES (?, ?)";
            jdbcTemplate.update(insertSql, userId, reviewId);

            String updateSql = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";
            jdbcTemplate.update(updateSql, reviewId);
        } else {
            throw new IllegalStateException("Пользователь уже поставил дизлайк к данному отзыву.");
        }
    }


    @Override
    public void deleteDislikeToReview(int reviewId, int userId) {
        String checkSql = "SELECT COUNT(*) FROM review_dislikes WHERE user_id = ? AND review_id = ?";
        int likeCount = jdbcTemplate.queryForObject(checkSql, Integer.class, userId, reviewId);

        if (likeCount > 0) {
            String deleteSql = "DELETE FROM review_dislikes WHERE user_id = ? AND review_id = ?";
            jdbcTemplate.update(deleteSql, userId, reviewId);

            String updateSql = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";
            jdbcTemplate.update(updateSql, reviewId);
        } else {
            throw new IllegalStateException("У пользователя нет дизлайка к данному отзыву.");
        }
    }


    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ?  WHERE review_id = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());
        feedSaveDao.saveEvent(review.getReviewId(), feedSaveDao.getEventTypeId(EventType.REVIEW), feedSaveDao.getOperationTypeId(OperType.UPDATE), review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    @Override
    public void deleteReview(int id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        int userId = getReviewById(id).getUserId();
        jdbcTemplate.update(sql, id);
        feedSaveDao.saveEvent(userId, feedSaveDao.getEventTypeId(EventType.REVIEW), feedSaveDao.getOperationTypeId(OperType.REMOVE), id);
    }

    @Override
    public Review getReviewById(int id) {
        String checkSql = "SELECT COUNT(*) FROM reviews WHERE review_id = ?";
        int reviewCount = jdbcTemplate.queryForObject(checkSql, Integer.class, id);
        if (reviewCount == 0) {
            throw new NotFoundException("Отзыв с указанным review_id не существует.");
        }
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRow, id);
    }


    @Override
    public List<Review> getReviewsByFilmId(Long filmId, Integer count) {
        String sql = "SELECT * FROM reviews";

        if (filmId != null) {
            sql += " WHERE film_id = ?";
        }
        sql += " ORDER BY useful DESC, review_id ASC";
        if (count != null) {
            sql += " LIMIT ?";
        }

        if (filmId != null && count != null) {
            return jdbcTemplate.query(sql, this::mapRow, filmId, count);
        } else if (filmId != null) {
            return jdbcTemplate.query(sql, this::mapRow, filmId);
        } else if (count != null) {
            return jdbcTemplate.query(sql, this::mapRow, count);
        } else {
            return jdbcTemplate.query(sql, this::mapRow);
        }
    }

    public Review mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Review review = new Review();
        review.setReviewId(resultSet.getInt("review_id")); //ID отзыва, генерируем
        review.setContent(resultSet.getString("content")); // Получаем в теле
        review.setIsPositive(resultSet.getBoolean("is_positive")); // если rating > 0 -> 1 or 0
        review.setUserId(resultSet.getInt("user_id"));// получаем от того кто оставил комент
        review.setFilmId(resultSet.getInt("film_id"));// получаем от того куда поставил комент
        review.setUseful(resultSet.getInt("useful"));// число лайков
        return review;
    }


    private void userAndFilmCheck(Review review) {
        if (review.getUserId() == 0 || review.getFilmId() == 0) {
            throw new ValidationException("Пользователь с указанным user_id не найден.");
        }
        String userCheckSql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        int userCount = jdbcTemplate.queryForObject(userCheckSql, Integer.class, review.getUserId());

        if (userCount == 0) {
            throw new NotFoundException("Пользователь с указанным user_id не найден.");
        }
        if (review.getContent() == null) {
            throw new InternalServerException("Отзыв не может быть пустым");
        }

        String filmCheckSql = "SELECT COUNT(*) FROM films WHERE film_id = ?";
        int filmCount = jdbcTemplate.queryForObject(filmCheckSql, Integer.class, review.getFilmId());

        if (filmCount == 0) {
            throw new NotFoundException("Пользователь с указанным user_id не найден.");
        }

    }
}


