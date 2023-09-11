package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;

@Component
public class FilmIdCounter {
    private int id;

    public int increaseFilmId() {
        return ++id;
    }

    // only tests
    public void setIdFilmCounter(int id) {
        this.id = id;
    }
}
