package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.*;

import javax.validation.constraints.*;

@Data
@NonNull
@NoArgsConstructor
@AllArgsConstructor

public class Film {

    private int id;
    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "name can not be empty")
    private String name;
    @NotNull(message = "Description cannot be null")
    @Size(max = 200, message = "description length cannot be more than 200 characters")
    private String description;
    @NotNull(message = "ReleaseDate cannot be null")
    @PastOrPresent(message = "the release date should be after December 28, 1895")
    private LocalDate releaseDate;
    @Positive(message = "the duration of the film should be positive")
    private int duration;
    public Set<Integer> likes;
    @NotEmpty(message = "genres can not be empty")
    @Size(min = 1, message = "at least one genre should be specified")
    private Collection<Genre> genres;
    @NotEmpty(message = "genres can not be empty")
    @Size(min = 1, message = "at least one genre should be specified")
    private Collection<Director> directors;
    private Mpa mpa;
    private Set<Mark> marks;

    public Film(String name, String description, LocalDate releaseDate, int duration, int id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = new HashSet<>();
        this.genres = new HashSet<>();
        this.directors = new HashSet<>();
        this.marks = new HashSet<>();
    }

}
