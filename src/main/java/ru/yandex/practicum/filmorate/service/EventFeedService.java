package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.EventFeedDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventFeed;
import ru.yandex.practicum.filmorate.model.EventTypes;
import ru.yandex.practicum.filmorate.model.Operations;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Service
public class EventFeedService {
    private final EventFeedDbStorage eventFeedDbStorage;
    private final UserStorage userStorage;

    public EventFeedService(EventFeedDbStorage eventFeedDbStorage, UserStorage userStorage) {
        this.eventFeedDbStorage = eventFeedDbStorage;
        this.userStorage = userStorage;
    }

    // Просмотр событий пользователя
    public List<EventFeed> viewRecentEvents(int userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.error("Пользователя с ID: {} не существует", userId);
            throw new NotFoundException("Пользователя с ID = " + userId + " не существует");
        }
        return eventFeedDbStorage.viewRecentEvents(userId);
    }

    // Добавление события в БД
    public void createEventFeed(int userId, EventTypes eventType, Operations operation, int entityId) {
        EventFeed eventFeed = EventFeed.builder()
                .userId(userId)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .eventType(eventType)
                .operation(operation)
                .entityId(entityId)
                .build();
        EventFeedDbStorage.createEventFeed(eventFeed);
    }
}
