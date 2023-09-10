package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public abstract class AbstractEntity {
    private long id;

    public AbstractEntity(long id) {
        this.id = id;
    }

    public AbstractEntity() {
    }

}
