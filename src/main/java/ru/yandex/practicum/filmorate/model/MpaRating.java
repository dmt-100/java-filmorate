package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MpaRating {

    private int id;
    @JsonProperty("name")
    private String title;
}
