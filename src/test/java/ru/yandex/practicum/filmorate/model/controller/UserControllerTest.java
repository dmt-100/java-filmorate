package ru.yandex.practicum.filmorate.model.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.service.IdCounter;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.*;


public class UserControllerTest {
    User user;
    User user2;
    UserController userController = new UserController();

    @BeforeEach
    void setUp() {
        user = User.builder()
                .login("dolore")
                .name("adipisicing")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
    }

    @AfterEach
    void tearDown() {
        userController.getUsers().remove(user);
        IdCounter.setIdUserCounter(0);
    }

    @Test
    void testCreateUserWithEmptyLogin() {
        user.setLogin("");
        Throwable exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("У пользователя некорректный логин: " + user.getLogin(), exception.getMessage());
    }

    @Test
    void testCreateUserWithEmptySpaceLogin() {
        user.setLogin("dolore ullamco");
        Throwable exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("У пользователя некорректный логин: " + user.getLogin(), exception.getMessage());
    }

    @Test
    void testCreateUserWithWrongEmail() {
        user.setEmail("mail.ru");
        Throwable exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("У пользователя некорректный емейл: " + user.getEmail(), exception.getMessage());
    }

    @Test
    void testCreateUserWithWrongBirthday() {
        user.setBirthday(LocalDate.of(2446, 8, 20));
        Throwable exception = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Некорректная дата рождения пользователя: " + user.getBirthday(), exception.getMessage());
    }

    @Test
    void testPutUserUpdate() {
        userController.createUser(user);
        user2 = User.builder()
                .login("doloreUpdate")
                .name("est adipisicing")
                .id(1)
                .email("mail@yandex.ru")
                .birthday(LocalDate.of(1976, 9, 20))
                .build();
        userController.putUser(user2);
        assertEquals(user, user2);
        assertEquals(1, userController.getUsers().size());
    }

    @Test
    void testPutUserUpdateWithWrongId() {
        userController.createUser(user);
        user2 = User.builder()
                .login("doloreUpdate")
                .name("est adipisicing")
                .id(9999)
                .email("mail@yandex.ru")
                .birthday(LocalDate.of(1976, 9, 20))
                .build();
        Throwable exception = assertThrows(ValidationException.class,
                () -> userController.putUser(user2));
        assertEquals("Некорректный id пользователя: " + user2.getId(), exception.getMessage());
    }


    @Test
    void testCreateUserWithEmptyName() {
        user.setName("");
        userController.createUser(user);
        User userExpected = userController.userRepository.getEqual(user, userController.getUsers());
        assertEquals(user.getLogin(), userExpected.getLogin());
        assertEquals(1, userController.getUsers().size());
    }

    @Test
    void testGetUsers() {
        userController.createUser(user);
        user2 = User.builder()
                .login("doloreUpdate")
                .name("est adipisicing")
                .email("mail@yandex.ru")
                .birthday(LocalDate.of(1976, 9, 20))
                .build();

        userController.createUser(user2);
        assertEquals(userController.userRepository.getUsers(), userController.getUsers());
    }

}