CREATE TABLE IF NOT EXISTS users (
    user_id  INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    VARCHAR(50) NOT NULL,
    login    VARCHAR(50) NOT NULL,
    name     VARCHAR(50),
    birthday DATE
);

CREATE TABLE IF NOT EXISTS films (
    film_id      INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR(50) NOT NULL,
    description  VARCHAR(255),
    release_date DATE,
    duration     INTEGER,
    rating_id    INTEGER
);

CREATE TABLE IF NOT EXISTS genres (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name     VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS rating (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title      VARCHAR(50) NOT NULL
);

ALTER TABLE films
    ADD FOREIGN KEY (rating_id) REFERENCES rating;

CREATE TABLE IF NOT EXISTS film_genres (
    film_id INTEGER NOT NULL
        REFERENCES films ON DELETE CASCADE,
    id INTEGER NOT NULL
        REFERENCES genres,
    PRIMARY KEY (film_id, id)
);

CREATE TABLE IF NOT EXISTS likes (
    film_id INTEGER NOT NULL
        REFERENCES films ON DELETE CASCADE,
    user_id INTEGER NOT NULL
        REFERENCES users,
    PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS friends (
    user_id INTEGER NOT NULL
        REFERENCES users ON DELETE CASCADE,
    friend_id INTEGER NOT NULL
        REFERENCES users,
    PRIMARY KEY (user_id, friend_id)
);