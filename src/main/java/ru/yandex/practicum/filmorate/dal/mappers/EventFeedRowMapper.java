package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventFeed;
import ru.yandex.practicum.filmorate.model.EventTypes;
import ru.yandex.practicum.filmorate.model.Operations;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventFeedRowMapper implements RowMapper<EventFeed> {
    @Override
    public EventFeed mapRow(ResultSet rs, int rowNum) throws SQLException {
        EventFeed eventFeed = new EventFeed();
        eventFeed.setEventId(rs.getInt("event_id"));
        eventFeed.setUserId(rs.getInt("user_id"));
        eventFeed.setTimestamp(rs.getTimestamp("times_tamp"));
        eventFeed.setEventType(EventTypes.valueOf(rs.getString("event_type")));
        eventFeed.setOperation(Operations.valueOf(rs.getString("operation")));
        eventFeed.setEntityId(rs.getInt("entity_id"));
        return eventFeed;
    }
}
