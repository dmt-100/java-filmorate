-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    user_name CHARACTER VARYING(100),
    user_email CHARACTER VARYING(100) NOT NULL UNIQUE,
    user_login CHARACTER VARYING(100) NOT NULL UNIQUE,
    user_birthday DATE
);

-- Создание таблицы рейтингов MPA
CREATE TABLE IF NOT EXISTS mpa_ratings (
    rating_id SERIAL PRIMARY KEY,
    rating CHARACTER VARYING(10) NOT NULL,
    description CHARACTER VARYING(200)
);

-- Создание таблицы фильмов
CREATE TABLE IF NOT EXISTS films (
    film_id integer not null primary key auto_increment,
     name CHARACTER VARYING(100) NOT NULL,
    description CHARACTER VARYING(200),
    release_date DATE,
    duration INTEGER,
    mpa_id INTEGER,

    CONSTRAINT films_mpa_fk FOREIGN KEY (mpa_id) REFERENCES mpa_ratings(rating_id)
);

-- Создание таблицы жанров
CREATE TABLE IF NOT EXISTS genres (
    genre_id SERIAL PRIMARY KEY,
    genre_name CHARACTER VARYING(100) NOT NULL
);

-- Создание связующей таблицы фильмов и жанров
CREATE TABLE IF NOT EXISTS films_genres (
    film_id INT NOT NULL,
    genre_id INT NOT NULL,

    PRIMARY KEY(film_id, genre_id),
    CONSTRAINT films_id_fk FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    CONSTRAINT genre_id_fk FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
);

-- Создание связующей таблицы для лайков фильмов
CREATE TABLE IF NOT EXISTS films_likes (
    film_id INT NOT NULL,
    user_id INT NOT NULL,

    CONSTRAINT fk_films_id FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);


-- Создание таблицы списка друзей
CREATE TABLE IF NOT EXISTS friend_list (
    user_id INT NOT NULL,
    friend_id INT NOT NULL,
    confirmed BOOLEAN,

    PRIMARY KEY(user_id, friend_id),
    CONSTRAINT user_id_fk FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT friend_id_fk FOREIGN KEY (friend_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS reviews (
    review_id SERIAL PRIMARY KEY,
    content CHARACTER VARYING(255) NOT NULL,
    is_positive BOOLEAN,
    user_id INT NOT NULL,
    film_id INT NOT NULL,
    useful INT DEFAULT 0,

    CONSTRAINT user_id_fk_reviews FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT film_id_fk_reviews FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS review_likes (
    like_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    review_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS review_dislikes (
    dislike_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    review_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT review_id_fk_reviews FOREIGN KEY (review_id) REFERENCES reviews(review_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS directors (
    director_id INT NOT NULL primary key auto_increment ,
    director_name CHARACTER VARYING(200)
);

CREATE TABLE IF NOT EXISTS films_director (
    film_id INT REFERENCES films(film_id) ON DELETE CASCADE,
    director_id INT REFERENCES directors(director_id) ON DELETE CASCADE
);

-- Создание таблицы типов событий
CREATE TABLE IF NOT EXISTS event_types (
    type_id SERIAL PRIMARY KEY,
    type_name CHARACTER VARYING(100) NOT NULL
);

-- Создание таблицы типов операций
CREATE TABLE IF NOT EXISTS operation_types (
    operation_id SERIAL PRIMARY KEY,
    operation_name CHARACTER VARYING(100) NOT NULL
);

-- Создание таблицы событий
CREATE TABLE IF NOT EXISTS events (
    event_id SERIAL PRIMARY KEY,
    time_stamp REAL NOT NULL,
    user_id INT NOT NULL,
    event_type INT NOT NULL,
    operation_type INT NOT NULL,
    entity_id INT NOT NULL,

    CONSTRAINT user_id_con FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT event_type_con FOREIGN KEY (event_type) REFERENCES event_types (type_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT operation_type_con FOREIGN KEY (operation_type) REFERENCES operation_types(operation_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS marks (
    user_id INT REFERENCES public.users(user_id) ON UPDATE CASCADE ON DELETE CASCADE,
    film_id INT REFERENCES public.films(film_id) ON UPDATE CASCADE ON DELETE CASCADE,
    mark integer NOT NULL DEFAULT 6,
    CONSTRAINT GRADES_PK PRIMARY KEY (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS friends (
    request_friend_id INT REFERENCES public.users(user_id) ON UPDATE CASCADE ON DELETE CASCADE,
    response_friend_id INT REFERENCES public.users(user_id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT FRIENDS_PK PRIMARY KEY (request_friend_id, response_friend_id)
);