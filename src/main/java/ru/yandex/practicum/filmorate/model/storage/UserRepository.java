package ru.yandex.practicum.filmorate.model.storage;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class UserRepository {

    private final Set<User> users = new LinkedHashSet<>();

    public User getEqual(User user, Set<User> users) {
        return users.stream().filter(user::equals).findAny().orElse(null);
    }

    public boolean isExist(int id) {
        boolean result = false;
        for (User user : users) {
            if (user.getId() == id) {
                result = true;
            }
        }
        return result;
    }

}
