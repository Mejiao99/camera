create database camera_storage;
CREATE USER camera_usr@localhost IDENTIFIED BY 'manzana546';
use camera_storage;
create table t_camera_storage
(
    uuid        varchar(64) not null
        primary key,
    raw_data    longblob    null,
    player_name varchar(20) null,
    pos_x       int         null,
    pos_y       int         null,
    pos_z       int         null,
    world_name  varchar(64) null,
    time        timestamp   null
);



