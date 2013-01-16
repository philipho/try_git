create table user
(
user_id MEDIUMINT NOT NULL AUTO_INCREMENT,
screen_name varchar(100) not null,
create_date timestamp null default current_timestamp,
primary key (user_id),
unique (screen_name)
) 

