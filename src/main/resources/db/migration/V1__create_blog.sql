create table captcha_codes (id integer not null auto_increment, code varchar(255), secret_code varchar(255), captcha_time timestamp, primary key (id)) engine=InnoDB;
create table global_settings (id integer not null auto_increment, code varchar(255), name varchar(255), value varchar(255), primary key (id)) engine=InnoDB;
create table post_comments (id integer not null auto_increment, parent_id integer, text Text, time_comment timestamp, post_id integer not null, user_id integer not null, primary key (id)) engine=InnoDB;
create table post_votes (id integer not null auto_increment, time_vote timestamp, value integer not null, post_id integer not null, user_id integer not null, primary key (id)) engine=InnoDB;
create table posts (id integer not null auto_increment, is_active integer, moderation_status varchar(255), text Text, time_post timestamp, title varchar(255), view_count integer, moderator_id integer, user_id integer not null, primary key (id)) engine=InnoDB;
create table posts_tags (posts_id integer not null, tags_id integer not null, primary key (posts_id, tags_id)) engine=InnoDB;
create table tags (id integer not null auto_increment, name varchar(255), primary key (id)) engine=InnoDB;
create table users (id integer not null auto_increment, code varchar(255), email varchar(255), is_moderator integer, name varchar(255), password varchar(255), photo Text, reg_time timestamp, primary key (id)) engine=InnoDB;
alter table post_comments add constraint FKaawaqxjs3br8dw5v90w7uu514 foreign key (post_id) references posts (id);
alter table post_comments add constraint FKsnxoecngu89u3fh4wdrgf0f2g foreign key (user_id) references users (id);
alter table post_votes add constraint FK9jh5u17tmu1g7xnlxa77ilo3u foreign key (post_id) references posts (id);
alter table post_votes add constraint FK9q09ho9p8fmo6rcysnci8rocc foreign key (user_id) references users (id);
alter table posts add constraint FK6m7nr3iwh1auer2hk7rd05riw foreign key (moderator_id) references users (id);
alter table posts add constraint FK5lidm6cqbc7u4xhqpxm898qme foreign key (user_id) references users (id);
alter table posts_tags add constraint FK79lx4quime8ct09nbmmf6wuao foreign key (tags_id) references tags (id);
alter table posts_tags add constraint FKi7se260d9epoxous2p6f1sl5k foreign key (posts_id) references posts (id);