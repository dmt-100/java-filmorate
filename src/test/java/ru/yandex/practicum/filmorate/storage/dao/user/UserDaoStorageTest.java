package ru.yandex.practicum.filmorate.storage.dao.user;

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
class UserDaoStorageTest {

    private final UserDaoStorage userDaoStorage;

    @Test
    void listUsers() {
        assertEquals(3, userDaoStorage.allUsers().size());
    }

    @Test
    void getUserById() {
        Optional<User> userOptional = Optional.ofNullable(userDaoStorage.getUserById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void createUser() {
        User user = new User();
        user.setEmail("email4");
        user.setLogin("login4");
        user.setBirthday(LocalDate.ofEpochDay(1985 - 5 - 5));
        user.setName("name4");
        userDaoStorage.createUser(user);
        assertEquals(4, userDaoStorage.allUsers().size());
    }

    @Test
    void updateUser() {
        User user = new User();
        user.setId(1);
        user.setEmail("update");
        user.setLogin("login4");
        user.setBirthday(LocalDate.ofEpochDay(1985 - 5 - 5));
        user.setName("update");
        userDaoStorage.updateUser(user);
        assertEquals("update", userDaoStorage.getUserById(1).getName());
    }

    @Test
    void addFriendPlusGetUserFriends() {
        userDaoStorage.addFriend(1, 2);
        assertEquals(1, userDaoStorage.getUserFriends(1).size());
    }

    @Test
    void deleteFriend() {
        userDaoStorage.addFriend(1, 2);
        userDaoStorage.addFriend(1, 3);
        userDaoStorage.deleteFriend(1, 3);
        assertEquals(1, userDaoStorage.getUserFriends(1).size());
    }

    @Test
    void getCommonFriendList() {
        userDaoStorage.addFriend(1, 2);
        userDaoStorage.addFriend(1, 3);
        userDaoStorage.addFriend(2, 3);
        List<User> friends = userDaoStorage.getCommonFriendList(1, 2);
        assertEquals(3, friends.get(0).getId());
    }
}