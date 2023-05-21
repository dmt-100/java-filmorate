package ru.yandex.practicum.filmorate.model.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.service.Validator;
import ru.yandex.practicum.filmorate.model.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.service.IdCounter;

import java.util.Set;

@Component
public class UserInMemoryStorage implements UserStorage {
    private final UserRepository userRepository = new UserRepository();

    @Override
    public Set<User> getUsers() {
        return userRepository.getUsers();
    }

    @Override
    public User getUserById(int id) {
        Validator.validateUserId(id);
        return userRepository.getUsers()
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
            for (User u : getUsers()) {
                if (u.getId() == user.getId()) {
                    u.setEmail(user.getEmail());
                    u.setLogin(user.getLogin());
                    u.setName(user.getName());
                    u.setBirthday(user.getBirthday());
                } else {
                    throw new ValidationException("Некорректный Id: " + user.getId());
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

    public UserRepository getUserRepository() {
        return userRepository;
    }

}
