package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.dao.DirectorDao;

import java.util.Collection;

@Service
public class DirectorService {
    private final DirectorDao directorDao;

    public DirectorService(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    public Collection<Director> getAllDirectors() {
        return directorDao.getAllDirectors();
    }

    public Director getDirectorById(Integer id) {
        return directorDao.getDirectorById(id);
    }

    public void deleteDirectorById(Integer id) {
        directorDao.deleteDirectorById(id);
    }

    public Director createDirector(Director director) {
        return directorDao.createDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorDao.updateDirector(director);
    }
}
