package ru.yandex.practicum.filmorate.storage.memory;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.service.Validator;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.IdCounter;

import java.util.*;

@Slf4j
@Data
@Component
public class InMemoryUserStorage implements UserStorage {
    private int usersIdCount = 0;
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<User, HashSet<Integer>> friends = new HashMap<>();

    @Override
    public List<User> listUsers() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(int id) {
        if (users.containsKey(id)) {
            log.debug("Текущий пользователь {}", users.get(id));
            return users.get(id);
        } else {
            log.warn("Ошибка запроса пользователя.");
            throw new ResourceNotFoundException("Ошибка запроса пользователя, проверьте корректность данных.");
        }
    }

    @Override
    public User createUser(@NonNull User user) {
        addNewId(user);
        if (Validator.validateUser(user))
            users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(@NonNull User user) {
        if (Validator.validateUser(user) && Validator.validateUserId(users.size(), user.getId())) {
            users.put(user.getId(), user);
        }
        return user;
    }

    @Override
    public void addFriend(int id, int friendId) {
        if (users.containsKey(id) && users.containsKey(friendId)) {
            User user1 = users.get(id);
            User user2 = users.get(friendId);
            friends.get(user1).add(friendId);
            friends.get(user2).add(id);
            log.debug("Теперь {} и {} друзья.", user1, user2);
        } else {
            log.warn("Ошибка при добавлении друга.");
            throw new ResourceNotFoundException("Ошибка добавления друга, проверьте корректность данных.");
        }
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        if (users.containsKey(id) && users.containsKey(friendId)) {
            User user1 = users.get(id);
            User user2 = users.get(friendId);
            friends.get(user1).remove(friendId);
            friends.get(user2).remove(id);
            log.debug("Теперь {} и {} не друзья.", user1, user2);
        } else {
            log.warn("Ошибка при удалении друга.");
            throw new ValidationException("Ошибка удаления друга, проверьте корректность данных.");
        }
    }

    @Override
    public List<User> getUserFriends(int id) {
        List<User> userFriends = new ArrayList<>();
        if (users.containsKey(id)) {
            User user = users.get(id);
            friends.get(user);
            List<Integer> friendsId = new ArrayList<>(friends.get(user));
            for (Integer userId : friendsId) {
                userFriends.add(users.get(userId));
            }
            return userFriends;
        } else {
            log.warn("Ошибка при получении списка друзей.");
            throw new ValidationException("Ошибка списка друзей, проверьте корректность данных.");
        }
    }

    @Override
    public List<User> getCommonFriendList(int id, int friendId) {
        List<User> commonFriendList = new ArrayList<>();
        if (users.containsKey(id) && users.containsKey(friendId)) {
            User user1 = users.get(id);
            User user2 = users.get(friendId);
            for (int friendsId : friends.get(user2)) {
                if (friends.get(user1).contains(friendsId)) {
                    commonFriendList.add(users.get(friendsId));
                }
            }
            return commonFriendList;
        } else {
            log.warn("Ошибка при получении списка общих друзей.");
            throw new ValidationException("Ошибка списка общих друзей, проверьте корректность данных.");
        }
    }

    private void addNewId(User user) {
        user.setId(IdCounter.increaseUserId());
    }
}
