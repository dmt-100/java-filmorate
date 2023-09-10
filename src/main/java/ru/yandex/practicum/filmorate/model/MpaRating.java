package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MpaRating extends AbstractEntity {

    //    private int id;
    @JsonProperty("name")
    private String title;

    public MpaRating(Long id, String title) {
        super(id);
        this.title = title;
    }

    public MpaRating() {
        this.title = "";
    }
}
