create table activity
(
activity_id MEDIUMINT NOT NULL AUTO_INCREMENT,
sender_id MEDIUMINT not null,
receiver_id MEDIUMINT not null,
txt_length decimal(3) not null,
create_date timestamp null default current_timestamp,
primary key (activity_id),
foreign key (sender_id) references user(user_id),
foreign key (receiver_id) references user(user_id)
);


#alter table activity add constraint activity_fk_sender_id foreign key (sender_id) references user (user_id);
#alter table activity add constraint activity_fk_receiver_id foreign key (receiver_id) references user (user_id);