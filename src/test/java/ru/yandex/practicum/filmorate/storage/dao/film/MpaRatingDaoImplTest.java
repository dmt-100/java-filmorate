package ru.yandex.practicum.filmorate.storage.dao.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class MpaRatingDaoImplTest {

    private final MpaRatingDaoImpl mpaRatingDao;

    @Test
    void getMpaRatings() {
        assertEquals(5, mpaRatingDao.getMpaRatings().size());
    }

    @Test
    void getMpaRatingById() {
        assertEquals("G", mpaRatingDao.getMpaRatingById(1).getTitle());
    }
}