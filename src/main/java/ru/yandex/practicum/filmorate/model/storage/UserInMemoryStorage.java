package ru.yandex.practicum.filmorate.model.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.interfaces.IUserStorage;
import ru.yandex.practicum.filmorate.model.service.Validator;
import ru.yandex.practicum.filmorate.model.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.service.IdCounter;

import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
@Component
public class UserInMemoryStorage implements IUserStorage {

    private final Set<User> users = new LinkedHashSet<>();

    public User getEqual(User user) {
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

    @Override
    public Set<User> getUsers() {
        return users;
    }

    @Override
    public User getUserById(int id) {
        Validator.validateUserId(users.size(), id);
        return getUsers()
                .stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public User createUser(User user) {
        if (Validator.validateUser(user)) {
            user.setId(IdCounter.increaseUserId());
            getUsers().add(user);
        }
        return getUserById(user.getId());

    }

    @Override
    public User putUser(User user) {
        User userUpdate = null;
        if (Validator.validateUser(user)) {
            Validator.validateUserId(users.size(), user.getId());
            for (User u : getUsers()) {
                if (u.getId() == user.getId()) {
                    u.setEmail(user.getEmail());
                    u.setLogin(user.getLogin());
                    u.setName(user.getName());
                    u.setBirthday(user.getBirthday());
                    u.setFriends(user.getFriends());
                } else {
                    throw new ValidationException("Некорректный id: " + user.getId());
                }
            }
            for (User user1 : getUsers()) {
                if (user1.equals(user)) {
                    userUpdate = user1;
                }
            }
        }
        return userUpdate;
    }


}
