package ru.yandex.practicum.filmorate.storage.feed;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Component
@Primary
public class FeedDB implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    public FeedDB(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Event> getFeed(int userId) {
        String sqlQuery = "SELECT e.*, " +
                "et.type_name as type_name, " +
                "ot.operation_name as operation_name " +
                "FROM events as e " +
                "JOIN event_types as et ON e. event_type = et.type_id " +
                "JOIN operation_types as ot ON e.operation_type = ot. operation_id " +
                "WHERE e.user_id = ?";
        List<Event> events = jdbcTemplate.query(sqlQuery, this::makeEvent, userId);
        return events;
    }

    private Event makeEvent(ResultSet resultSet, int i) throws SQLException {
        return new Event(
                resultSet.getInt("event_id"),
                resultSet.getLong("time_stamp"),
                resultSet.getInt("user_id"),
                resultSet.getString("type_name"),
                resultSet.getString("operation_name"),
                resultSet.getInt("entity_id")
        );
    }
}
