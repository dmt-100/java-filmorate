package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

    @Test
    void listUsers() {
        assertEquals(3, userDbStorage.listUsers().size());
    }

    @Test
    void getUserById() {
        Optional<User> userOptional = Optional.ofNullable(userDbStorage.getUserById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void createUser() {
        User user = new User();
        user.setEmail("email4");
        user.setLogin("login4");
        user.setBirthday(LocalDate.ofEpochDay(1985 - 5 - 5));
        user.setName("name4");
        userDbStorage.createUser(user);
        assertEquals(4, userDbStorage.listUsers().size());
    }

    @Test
    void updateUser() {
        User user = new User();
        user.setId(1);
        user.setEmail("update");
        user.setLogin("login4");
        user.setBirthday(LocalDate.ofEpochDay(1985 - 5 - 5));
        user.setName("update");
        userDbStorage.updateUser(user);
        assertEquals("update", userDbStorage.getUserById(1).getName());
    }

    @Test
    void addFriendPlusGetUserFriends() {
        userDbStorage.addFriend(1, 2);
        assertEquals(1, userDbStorage.getUserFriends(1).size());
    }

    @Test
    void deleteFriend() {
        userDbStorage.addFriend(1, 2);
        userDbStorage.addFriend(1, 3);
        userDbStorage.deleteFriend(1, 3);
        assertEquals(1, userDbStorage.getUserFriends(1).size());
    }

    @Test
    void getCommonFriendList() {
        userDbStorage.addFriend(1, 2);
        userDbStorage.addFriend(1, 3);
        userDbStorage.addFriend(2, 3);
        List<User> friends = userDbStorage.getCommonFriendList(1, 2);
        assertEquals(3, friends.get(0).getId());
    }
}