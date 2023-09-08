package ru.yandex.practicum.filmorate.storage.dao.user;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ValidationException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@AllArgsConstructor
@Component
public class UserDaoStorage implements UserStorage {

    private static final String GET_ALL_USERS = "SELECT * FROM users";
    private static final String GET_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";
    private static final String CREATE_USER = "INSERT INTO users (email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";

    private static final String GET_USER_FRIENDS = "SELECT * FROM users u JOIN (SELECT friend_id" +
            " FROM friends WHERE user_id = ?) l ON u.user_id = l.friend_id";
    private static final String GET_COMMON_FRIENDLIST = "SELECT * FROM users u" +
            " JOIN (SELECT friend_id FROM friends WHERE user_id = ?) f ON u.user_id = f.friend_id" +
            " JOIN (SELECT friend_id FROM friends WHERE user_id = ?) l ON u.user_id = l.friend_id";

    private final JdbcTemplate jdbcTemplate;
    private final FriendDao friendDao;

    @Override
    public List<User> listUsers() {
        return jdbcTemplate.query(GET_ALL_USERS, this::mapRowToUser);
    }

    @Override
    public User getUserById(int id) {
        return jdbcTemplate.queryForObject(GET_USER_BY_ID, this::mapRowToUser, id);
    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(CREATE_USER, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            user.setId((Integer) keyHolder.getKey());
        } else {
            throw new ValidationException("Ошибка генерации id в базе.");
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update(UPDATE_USER, user.getEmail(), user.getLogin(),
                user.getName(), Date.valueOf(user.getBirthday()), user.getId());
        return user;
    }

    @Override
    public void addFriend(int id, int friendId) {
        friendDao.addFriend(id, friendId);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        friendDao.deleteFriend(id, friendId);
    }

    @Override
    public List<User> getUserFriends(int id) {
        return jdbcTemplate.query(GET_USER_FRIENDS, this::mapRowToUser, id);
    }

    @Override
    public List<User> getCommonFriendList(int id, int friendId) {
        return jdbcTemplate.query(GET_COMMON_FRIENDLIST, this::mapRowToUser, id, friendId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) {
        try {
            User user = new User();
            user.setId(resultSet.getInt("user_id"));
            user.setEmail(resultSet.getString("email"));
            user.setLogin(resultSet.getString("login"));
            user.setName(resultSet.getString("name"));
            user.setBirthday(resultSet.getDate("birthday").toLocalDate());
            return user;
        } catch (EmptyResultDataAccessException | SQLException e) {
            throw new ResourceNotFoundException("Ошибка запроса, проверьте корректность данных.");
        }
    }
}
