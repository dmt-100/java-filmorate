package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

public interface FeedStorage {
    Collection<Event> getFeed(int userId);
}
