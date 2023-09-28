package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;

import java.util.Collection;

@Service
public class FeedService {
    private final FeedStorage feedStorage;
    private final UserService userService;

    public FeedService(FeedStorage feedStorage, UserService userService) {
        this.feedStorage = feedStorage;
        this.userService = userService;
    }

    public Collection<Event> getFeed(int id) {
        userService.findUser(id); // метод осуществляет проверку наличия пользователя с переданным id
        return feedStorage.getFeed(id);
    }
}
