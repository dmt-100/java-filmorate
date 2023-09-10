package ru.yandex.practicum.filmorate.storage.memory;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserIdCounter;
import ru.yandex.practicum.filmorate.service.Validator;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Data
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<User, HashSet<Long>> friends = new HashMap<>();
    private final Validator validator;
    private final UserIdCounter userIdCounter;

    @Override
    public List<User> allUsers() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long id) {
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
        user.setId(userIdCounter.increaseUserId());
        if (validator.validateUser(user))
            users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(@NonNull User user) {
        if (validator.validateUser(user) && validator.validateUserId(users.size(), user.getId())) {
            users.put(user.getId(), user);
        }
        return user;
    }

    @Override
    public void addFriend(long id, long friendId) {
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
    public void deleteFriend(long id, long friendId) {
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
    public List<User> getUserFriends(long id) {
        List<User> userFriends = new ArrayList<>();
        if (users.containsKey(id)) {
            User user = users.get(id);
            friends.get(user);
            List<Long> friendsId = new ArrayList<>(friends.get(user));
            for (Long userId : friendsId) {
                userFriends.add(users.get(userId));
            }
            return userFriends;
        } else {
            log.warn("Ошибка при получении списка друзей.");
            throw new ValidationException("Ошибка списка друзей, проверьте корректность данных.");
        }
    }

    @Override
    public List<User> getCommonFriendList(long id, long friendId) {
        List<User> commonFriendList = new ArrayList<>();
        if (users.containsKey(id) && users.containsKey(friendId)) {
            User user1 = users.get(id);
            User user2 = users.get(friendId);
            for (long friendsId : friends.get(user2)) {
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

}
