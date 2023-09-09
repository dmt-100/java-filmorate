package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;

@Component
public class UserIdCounter {
    private int id;

    public int increaseUserId() {
        return ++id;
    }
    // only tests
    public void setId(int id) {
        this.id = id;
    }
    // only tests
    public void setIdUserCounter(int id) {
        this.id = id;
    }

}
