package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Film {

    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Значение должно быть положительным")
    private int duration;
    private int rate;
    @NonNull
    private Set<Integer> likes;

}
