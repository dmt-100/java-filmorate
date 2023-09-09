package ru.yandex.practicum.filmorate.storage.memory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserIdCounter;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserControllerTest {
    private User user;
    private User user2;

    @Autowired
    private InMemoryUserStorage inMemoryUserStorage;

    private UserIdCounter userIdCounter;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setLogin("dolore");
        user.setName("adipisicing");
        user.setEmail("mail@mail.ru");
        user.setBirthday(LocalDate.of(1946, 8, 20));
    }

    @AfterEach
    void tearDown() {
        userIdCounter = new UserIdCounter();
        inMemoryUserStorage.getUsers().clear();
        userIdCounter.setIdUserCounter(0);
    }

    @Test
    void testCreateUserWithEmptyLogin() {
        user.setLogin("");
        Throwable exception = assertThrows(ValidationException.class,
                () -> inMemoryUserStorage.createUser(user));
        assertEquals("У пользователя некорректный логин: " + user.getLogin(), exception.getMessage());
    }

    @Test
    void testCreateUserWithEmptySpaceLogin() {
        user.setLogin("dolore ullamco");
        Throwable exception = assertThrows(ValidationException.class,
                () -> inMemoryUserStorage.createUser(user));
        assertEquals("У пользователя некорректный логин: " + user.getLogin(), exception.getMessage());
    }

    @Test
    void testCreateUserWithWrongEmail() {
        user.setEmail("mail.ru");
        Throwable exception = assertThrows(ValidationException.class,
                () -> inMemoryUserStorage.createUser(user));
        assertEquals("У пользователя некорректный емейл: " + user.getEmail(), exception.getMessage());
    }

    @Test
    void testCreateUserWithWrongBirthday() {
        user.setBirthday(LocalDate.of(2446, 8, 20));
        Throwable exception = assertThrows(ValidationException.class,
                () -> inMemoryUserStorage.createUser(user));
        assertEquals("Некорректная дата рождения пользователя: " + user.getBirthday(), exception.getMessage());
    }

    @Test
    void testPutUserUpdate() {

        inMemoryUserStorage.createUser(user);
        user2 = new User();
        user2.setLogin("doloreUpdate");
        user2.setName("est adipisicing");
        user2.setId(1);
        user2.setEmail("mail@yandex.ru");
        user2.setBirthday(LocalDate.of(1976, 9, 20));
        inMemoryUserStorage.updateUser(user2);
        assertEquals(user2, user2);
        assertEquals(1, inMemoryUserStorage.allUsers().size());
    }

    @Test
    void testPutUserUpdateWithWrongId() {
        inMemoryUserStorage.createUser(user);
        user2 = new User();
        user2.setLogin("doloreUpdate");
        user2.setName("est adipisicing");
        user2.setId(9999);
        user2.setEmail("mail@yandex.ru");
        user2.setBirthday(LocalDate.of(1976, 9, 20));
        Throwable exception = assertThrows(ValidationException.class,
                () -> inMemoryUserStorage.updateUser(user2));
        assertEquals("Некорректный id пользователя: " + user2.getId(), exception.getMessage());
    }

    @Test
    void testCreateUserWithEmptyName() {
        user.setLogin("dolore");
        user.setName("");
        user.setEmail("mail@mail.ru");
        user.setBirthday(LocalDate.of(1946, 8, 20));
        inMemoryUserStorage.createUser(user);
        String actual = user.getName();
        String name = inMemoryUserStorage.getUserById(user.getId()).getName();
        assertEquals("dolore", actual);
    }

    @Test
    void testGetUsers() {
        inMemoryUserStorage.createUser(user);
        User user2 = new User();
        user2.setLogin("doloreUpdate");
        user2.setName("est adipisicing");
        user2.setEmail("mail@yandex.ru");
        user2.setBirthday(LocalDate.of(1976, 9, 20));
        inMemoryUserStorage.createUser(user2);
        assertEquals(2, inMemoryUserStorage.allUsers().size());
    }

}