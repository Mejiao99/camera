create database camera_storage;
CREATE USER camera_usr@localhost IDENTIFIED BY 'manzana546';
use camera_storage;
CREATE TABLE t_camera_storage
(
    uuid            varchar(64),
    raw_data        longblob,
    player_name     varchar(20),
    pos_x            int(11),
    pos_y           int(11),
    pos_z           int(11),
    world_name      varchar(64),
    time            timestamp
);

alter table t_camera_storage
    add constraint t_camera_storage_pk
        primary key (uuid);


insert into t_camera_storage
values (?, ?, ?, ?, ?, ?, ?, ?);



select uuid, raw_data from t_camera_storage where uuid = ?;
select raw_data from t_camera_storage where uuid = ?;



select 1 + 1;
select 'hello world';
select 1 + 1 as suma;


