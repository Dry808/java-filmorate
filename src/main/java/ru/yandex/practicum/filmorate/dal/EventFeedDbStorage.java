package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.EventFeed;

import java.util.List;

@Slf4j
@Repository
@Primary
public class EventFeedDbStorage extends BaseRepository<EventFeed> {
    private static final String FIND_EVENT_FEED = "SELECT * FROM EVENT_FEED ef WHERE ef.USER_ID = ?";
    private static final String INSERT_EVENT_FEED = "INSERT INTO event_feed (user_id, times_tamp, event_type, operation," +
            " entity_id) VALUES (?, ?, ?, ?, ?)";
    private static JdbcTemplate jdbcTemplate;

    public EventFeedDbStorage(JdbcTemplate jdbc, RowMapper<EventFeed> mapper, JdbcTemplate jdbcTemplate) {
        super(jdbc, mapper);
        this.jdbcTemplate = jdbcTemplate;
    }

    // Просмотр сабытий пользователя
    public List<EventFeed> viewRecentEvents(int userId) {
        return findMany(FIND_EVENT_FEED, userId);
    }

    // Запись в БД события
    public static void createEventFeed(EventFeed eventFeed) {
        jdbcTemplate.update(
                INSERT_EVENT_FEED,
                eventFeed.getUserId(),
                eventFeed.getTimestamp(),
                eventFeed.getEventType().name(),
                eventFeed.getOperation().name(),
                eventFeed.getEntityId()
        );
    }
}
