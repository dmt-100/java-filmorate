package ru.yandex.practicum.filmorate.model.storage;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {


    Set<User> getUsers();
    User getUserById(int id);
    User createUser(User user);
    User putUser(User user);
}
