package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Genre extends AbstractEntity {

    //    private int id;
    private String name;
}
