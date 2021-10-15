create database camera_storage;
CREATE USER camera_usr@localhost IDENTIFIED BY 'manzana546';
use camera_storage;
create table t_camera_storage
(
    uuid        varchar(64)  not null
        primary key,
    raw_data    longblob     null,
    player_name varchar(256) null,
    pos_x       double       null,
    pos_y       double       null,
    pos_z       double       null,
    world_name  varchar(256) null,
    time        timestamp    null
);