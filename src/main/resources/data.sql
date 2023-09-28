-- Вставка данных в таблицу жанров
MERGE INTO genres (GENRE_ID, GENRE_NAME)
values (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');
MERGE INTO MPA_RATINGS (RATING_ID, RATING, DESCRIPTION)
 VALUES (1, 'G', 'Нет возрастных ограничений'),
           (2, 'PG', 'Рекомендуется присутствие родителей'),
           (3, 'PG-13', 'Детям до 13 лет просмотр не желателен'),
           (4, 'R', 'Лицам до 17 лет обязательно присутствие взрослого'),
           (5, 'NC-17', 'Лицам до 18 лет просмотр запрещен');
-- Вставка данных в таблицу типов событий
MERGE INTO event_types (type_id, type_name)
values (1, 'LIKE'),
       (2, 'REVIEW'),
       (3, 'FRIEND');
-- Вставка данных в таблицу типов операций
MERGE INTO operation_types (operation_id, operation_name)
 VALUES (1, 'REMOVE'),
        (2, 'ADD'),
        (3, 'UPDATE');

