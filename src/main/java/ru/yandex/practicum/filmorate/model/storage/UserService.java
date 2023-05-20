package ru.yandex.practicum.filmorate.model.storage;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Service
public class UserService {

    public User addFriend(InMemoryUserStorage storage, int id, int friendId) {

        checkFriendsOfNull(storage, id, friendId);

        storage.getUserById(id).getFriends().add(friendId);
        storage.getUserById(friendId).getFriends().add(id); // ТЗ-10: Пока пользователям не надо одобрять заявки в друзья — добавляем сразу. То есть если Лена стала другом Саши, то это значит, что Саша теперь друг Лены
        return storage.getUserById(id);
    }

    public User deleteFriend(InMemoryUserStorage storage, int id, int friendId) {
        storage.getUserById(id).getFriends().remove(friendId);
        storage.getUserById(friendId).getFriends().remove(id);
        return storage.getUserById(id);
    }

    public Set<User> getCommonFriendsById(InMemoryUserStorage storage, int id, int otherId) {
        Set<User> result = new LinkedHashSet<>();

        checkFriendsOfNull(storage, id, otherId);

        if (storage.getUserById(id).getFriends().size() == 0) {
            return result;
        }
        for (Integer friend1 : storage.getUserById(id).getFriends()) {
            for (Integer friend2 : storage.getUserById(otherId).getFriends()) {
                if (Objects.equals(friend1, friend2)) {
                    result.add(storage.getUserById(friend1));
                }
            }
        }
        return result;
//        List<Integer> result;
//        getUsers().stream().map(u -> getUserById(id).getFriends().stream().filter(f -> for))
    }

    public List<User> getFriends(InMemoryUserStorage storage, int id) {
        List<User> users = new ArrayList<>();
        for (Integer friendId : storage.getUserById(id).getFriends()) {
            users.add(storage.getUserById(friendId));
        }
        return users;
    }

    private void checkFriendsOfNull(InMemoryUserStorage storage, int id, int friendId) {
        if (storage.getUserById(id).getFriends() == null) {
            storage.getUserById(id).setFriends(new HashSet<>());
        }
        if (storage.getUserById(friendId).getFriends() == null) {
            storage.getUserById(friendId).setFriends(new HashSet<>());
        }
    }
}
