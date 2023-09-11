package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.dao.film.MpaRatingDao;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class MpaRatingService {
    private final MpaRatingDao mpaRatingDao;

    public List<MpaRating> getMpaRatings() {
        return mpaRatingDao.getMpaRatings();
    }

    public MpaRating getMpaRatingById(int id) {
        try {
            return mpaRatingDao.getMpaRatingById(id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Ошибка запроса рейтинга.");
            throw new ResourceNotFoundException("Ошибка запроса рейтинга, проверьте корректность данных.");
        }
    }
}
