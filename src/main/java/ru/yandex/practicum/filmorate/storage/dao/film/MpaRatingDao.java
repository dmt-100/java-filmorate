package ru.yandex.practicum.filmorate.storage.dao.film;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public interface MpaRatingDao {
    List<MpaRating> getMpaRatings();

    MpaRating getMpaRatingById(int id);
}
