drop table if exists captcha_codes
drop table if exists global_settings
drop table if exists post_comments
drop table if exists post_votes
drop table if exists posts
drop table if exists tag2post
drop table if exists tags
drop table if exists users
create table captcha_codes (id integer not null auto_increment, code varchar(255), secret_code varchar(255), time datetime(6), primary key (id)) engine=InnoDB
create table global_settings (id integer not null auto_increment, code varchar(255), name varchar(255), value varchar(255), primary key (id)) engine=InnoDB
create table post_comments (id integer not null auto_increment, parent_id integer, post_id integer, text varchar(255), time datetime(6), user_id integer not null, primary key (id)) engine=InnoDB
create table post_votes (id integer not null auto_increment, post_id integer, time datetime(6), value integer not null, user_id integer not null, primary key (id)) engine=InnoDB
create table posts (id integer not null auto_increment, is_active integer, status varchar(255), text varchar(255), time datetime(6), title varchar(255), view_count integer, moderator_id integer not null, user_id integer not null, primary key (id)) engine=InnoDB
create table tag2post (id integer not null, post_id integer not null auto_increment, tag_id integer not null, primary key (post_id, tag_id)) engine=InnoDB
create table tags (id integer not null auto_increment, name varchar(255), primary key (id)) engine=InnoDB
create table users (id integer not null auto_increment, code varchar(255), email varchar(255), is_moderator bit, name varchar(255), password varchar(255), photo varchar(255), reg_time datetime(6), primary key (id)) engine=InnoDB
alter table post_comments add constraint FKsnxoecngu89u3fh4wdrgf0f2g foreign key (user_id) references users (id)
alter table post_votes add constraint FK9q09ho9p8fmo6rcysnci8rocc foreign key (user_id) references users (id)
alter table posts add constraint FK6m7nr3iwh1auer2hk7rd05riw foreign key (moderator_id) references users (id)
alter table posts add constraint FK5lidm6cqbc7u4xhqpxm898qme foreign key (user_id) references users (id)
alter table tag2post add constraint FK4qq5n9ys2a760c3k3offw4tqp foreign key (post_id) references tags (id)
alter table tag2post add constraint FKjou6suf2w810t2u3l96uasw3r foreign key (tag_id) references tags (id)