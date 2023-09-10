package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Getter
@Setter
@RequiredArgsConstructor
public class User extends AbstractEntity {

    //    private int id;
    @NotNull
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
    private Set<Integer> friends = new LinkedHashSet<>();

    public boolean isEmptyName() {
        if (name == null || name.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
}
