package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class Review {
    int reviewId;
    @NotEmpty
    String content;
    @NotNull(message = "userId cannot be null")
    @Positive(message = "userId cannot be 0 or >0")
    int userId;
    @NotNull(message = "filmId cannot be null")
    @Positive(message = "filmId cannot be 0 or >0")
    int filmId;
    int useful;
    Boolean isPositive;

    public Boolean getIsPositive() {
        return isPositive;
    }
}
