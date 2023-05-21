package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class User {

    private int id;
    @Email
    private String email;
    @NonNull
    private String login;
    private String name;
    private LocalDate birthday;
    @Singular
    private Set<Integer> friends;

    public boolean isEmptyName() {
        if (name == null || name.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
}
