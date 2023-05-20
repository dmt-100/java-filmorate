package ru.yandex.practicum.filmorate.model.storage;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

/*
Классы-сервисы должны иметь доступ к классам-хранилищам. Убедитесь, что сервисы зависят от интерфейсов классов-хранилищ, а не их реализаций. Таким образом в будущем будет проще добавлять и использовать новые реализации с другим типом хранения данных.
Сервисы должны быть внедрены в соответствующие контроллеры.
 */
//@Data
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final FilmRepository filmRepository = new FilmRepository();

    @Override
    public Set<Film> getFilms() {
        return filmRepository.getFilms();
    }

    @Override
    public Film getFilmById(int id) {
        return filmRepository.getFilmById(id);
    }

    @Override
    public void setFilm(Film film) {
        getFilms().add(film);
    }

    @Override
    public void addLike(Film film, int id) {
        if (film.equals(filmRepository.getEqual(film, getFilms()))) {
            film.getLikes().add(id);
        } else {
            System.out.println("Фильм не найден");
        }
    }

    @Override
    public void removeLike(Film film, int id) {
        if (film.equals(filmRepository.getEqual(film, getFilms()))) {
            film.getLikes().remove(id);
        } else {
            System.out.println("Фильм не найден");
        }
    }

    @Override
    public Film addLikeToFilm(int id, int userId) {
        for (Film film : getFilms()) {
            if (film.getId() == id) {
                film.setLikes(new HashSet<>());
                film.getLikes().add(userId);
            }
        }
        return filmRepository.getFilmById(id);
    }




}
