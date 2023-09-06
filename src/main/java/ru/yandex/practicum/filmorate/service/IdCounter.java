package ru.yandex.practicum.filmorate.service;

public class IdCounter {

    private static int idUserCounter;
    private static int idFilmCounter;

    public static int increaseUserId() {
        return ++idUserCounter;
    }

    public static int increaseFilmId() {
        return ++idFilmCounter;
    }

    public static void setIdUserCounter(int idUserCounter) {
        IdCounter.idUserCounter = idUserCounter;
    }

    public static void setIdFilmCounter(int idFilmCounter) {
        IdCounter.idFilmCounter = idFilmCounter;
    }
}
