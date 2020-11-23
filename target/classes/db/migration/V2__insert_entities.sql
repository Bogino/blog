insert into users (is_moderator, reg_time, name, email, password, code, photo) values(false, CURRENT_DATE, "Vanya", "ivan@yandex.ru", "123", NULL, NULL);
insert into users (is_moderator, reg_time, name, email, password, code, photo) values(false, CURRENT_DATE, "Sanya", "san@yandex.ru", "1234", NULL, NULL);
insert into posts (is_active, moderation_status, text, time, title, view_count, moderator_id, user_id) values(true, "NEW", "Текст поста от Вани по Java", CURRENT_DATE, "Заголовок от Вани", 154, NULL, 1);
insert into posts (is_active, moderation_status, text, time, title, view_count, moderator_id, user_id) values(true, "NEW", "Текст поста от Сани по C#", CURRENT_DATE, "Заголовок от Сани", 150, NULL, 2);
insert into posts (is_active, moderation_status, text, time, title, view_count, moderator_id, user_id) values(true, "NEW", "Текст поста от Сани по Еде", CURRENT_DATE, "Заголовок от Сани про еду", 120, NULL, 2);
insert into post_comments (parent_id, post_id, text, time, user_id) values(NULL, 1, "Хороший пост, мне очень нравится между прочим, это Люба говорит", CURRENT_DATE, 1);
insert into post_comments (parent_id, post_id, text, time, user_id) values(1, 1, "Cогласен с тобой", CURRENT_DATE, 2);
insert into tags (name) values("Программрование");
insert into tags (name) values("Back-end");
insert into tags (name) values("Хавчик");
insert into tag2Post (post_id, tag_id) values(1, 1);
insert into tag2Post (post_id, tag_id) values(1, 2);
insert into tag2Post (post_id, tag_id) values(2, 1);
insert into tag2Post (post_id, tag_id) values(2, 2);
insert into tag2Post (post_id, tag_id) values(3, 3);







