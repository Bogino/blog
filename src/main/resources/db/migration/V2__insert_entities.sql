insert into users (is_moderator, reg_time, name, email, password, code, photo) values
(0, CURRENT_DATE, "Vanya", "ivan@yandex.ru", "$2y$12$iUMhVI/FnyqD.xO6OqYIp.8fEcypTMTSSGN0Dgjt96sCAUVmDk45m", NULL, NULL),
(1, CURRENT_DATE, "Саня", "san@yandex.ru", "$2y$12$p5JCHkyThXQ6rgHukJQtxeKGURVq5Gh8fxkpSp7cPKlLK4.aIhI1W", NULL, NULL),
(1, CURRENT_DATE, "Илья", "ibogino@yandex.ru", "$2y$12$Im5T.yooVrI18uQWvdDIXuEoPsSUfMDYt8oBZOYDrDeO.WhDHAnNm", NULL, NULL);

insert into posts (is_active, moderation_status, text, time, title, view_count, moderator_id, user_id) values(true, "ACCEPTED", "Текст поста от Вани по Java", "2020-12-01 12:34:45", "Заголовок от Вани", 154, 2, 1), (true, "ACCEPTED", "Текст поста от Сани по C#", "2020-12-02 12:34:45", "Заголовок от Сани", 150, 2, 2),
(true, "ACCEPTED", "Текст поста от Сани по Еде", "2020-12-03 12:34:45", "Заголовок от Сани про еду", 120, 2, 2), (true, "ACCEPTED", "Второй текст поста от Сани по Еде", "2020-12-04 12:34:45", "Заголовок второго посата от Сани про еду", 120, 2, 1),
(true, "ACCEPTED", "Третий текст поста от Ильи по Еде", "2020-12-05 12:34:45", "Заголовок третьего посата от Ильи про еду", 120, 2, 3), (true, "NEW", "Четвертый текст поста от Сани по Еде", "2020-12-06 12:34:45", "Заголовок четвертого посата от Сани про еду", 120, 2, 1),
(true, "NEW", "Пятый текст поста от Сани по Еде", "2020-12-07 12:34:45", "Заголовок пятого посата от Сани про еду", 120, 2, 1), (true, "NEW", "Шестой текст поста от Сани по Еде", "2020-12-08 12:34:45", "Заголовок шестого посата от Сани про еду", 120, 2, 1),
(true, "NEW", "Седьмой текст поста от Сани по Еде", "2020-12-09 12:34:45", "Заголовок седьмого посата от Сани про еду", 120, NULL, 2), (true, "NEW", "Восьмой текст поста от Сани по Еде", "2020-12-10 12:34:45", "Заголовок восьмого посата от Сани про еду", 120, 2, 1),
(true, "NEW", "Девятый текст поста от Сани по Еде", "2020-12-11 12:34:45", "Заголовок 9 посата от Сани про еду", 120, NULL, 2), (true, "NEW", "Десятый текст поста от Сани по Еде", "2020-12-12 12:34:45", "Заголовок 10 посата от Сани про еду", 120, NULL, 2),
(true, "NEW", "Одинадцатый текст поста от Сани по Еде", "2020-12-13 12:34:45", "Заголовок 11 посата от Сани про еду", 120, NULL, 2), (true, "NEW", "Двенадцатый текст поста от Сани по Еде", "2020-12-14 12:34:45", "Заголовок 12 посата от Сани про еду", 120, 2, 1);

insert into post_comments (parent_id, post_id, text, time, user_id) values(NULL, 1, "Хороший пост, мне очень нравится между прочим, это Люба говорит", CURRENT_DATE, 1), (1, 1, "Cогласен с тобой", CURRENT_DATE, 2);

insert into post_votes (post_id, time, value, user_id) values(1, CURRENT_DATE, 1, 2), (2, CURRENT_DATE, 1, 2), (3, CURRENT_DATE, 1, 2), (4, CURRENT_DATE, 1, 2), (5, CURRENT_DATE, 1, 2),
(6, CURRENT_DATE, 1, 1);

insert into global_settings (code, name, value) values("YES", "YES", "YES");