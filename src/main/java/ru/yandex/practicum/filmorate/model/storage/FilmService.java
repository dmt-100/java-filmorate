package ru.yandex.practicum.filmorate.model.storage;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();

    public List<Film> getMostPopularFilms(int count) {
        List<Film> result;
        for (Film film : inMemoryFilmStorage.getFilms()) {

        }

        result = inMemoryFilmStorage.getFilms().stream()
                .filter(f -> f.getLikes().size() > count)
                .sorted()
                .collect(Collectors.toList());
        return result;
    }
}
