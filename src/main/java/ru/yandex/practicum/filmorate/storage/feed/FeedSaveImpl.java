package ru.yandex.practicum.filmorate.storage.feed;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.feed.OperType;

import java.time.Instant;

@Component
@Primary
public class FeedSaveImpl implements FeedSaveDao {
    private final JdbcTemplate jdbcTemplate;

    public FeedSaveImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveEvent(int userId, int eventType, int operationType, int entityId) {
        String sqlQuery = "INSERT INTO events (time_stamp, user_id, event_type, operation_type, entity_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, Instant.now().toEpochMilli(), userId, eventType, operationType, entityId);
    }

    @Override
    public int getEventTypeId(EventType eventType) {
        switch (eventType) {
            case LIKE:
                return 1;
            case REVIEW:
                return 2;
            case FRIEND:
                return 3;
            default:
                throw new ValidationException("No such event type found.");
        }
    }

    @Override
    public int getOperationTypeId(OperType operationType) {
        switch (operationType) {
            case REMOVE:
                return 1;
            case ADD:
                return 2;
            case UPDATE:
                return 3;
            default:
                throw new ValidationException("No such operation type found.");
        }
    }
}
