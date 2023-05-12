package ru.yandex.practicum.filmorate.model.repository;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserRepository {

    private Set<User> users = new HashSet<>();

    public User getEqual(User user, Set<User> users) {
        return users.stream().filter(user::equals).findAny().orElse(null);
    }

}
