package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class User {

    private int id;
    //    @Email
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public boolean isEmptyName() {
        if (name == null) {
            return true;
        } else {
            return false;
        }
    }
}
