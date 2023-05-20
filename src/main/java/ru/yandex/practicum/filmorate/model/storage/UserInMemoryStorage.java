package ru.yandex.practicum.filmorate.model.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.service.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.service.IdCounter;

import java.util.Set;

@Component
public class UserInMemoryStorage implements UserStorage{
    private final UserRepository userRepository = new UserRepository();

    @Override
    public Set<User> getUsers() {
        return userRepository.getUsers();
    }

    @Override
    public User getUserById(int id) {
        return userRepository.getUsers()
                .stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }
    @Override
    public User createUser(User user) {
        user.setId(IdCounter.increaseUserId());
        getUsers().add(user);
        return getUserById(user.getId());
    }

    @Override
    public User putUser(User user) {
        User result = null;
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
                result = user1;
            }
        }
        return result;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

}
