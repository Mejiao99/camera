create database camera_storage;
CREATE USER camera_usr@localhost IDENTIFIED BY 'manzana546';
use camera_storage;
CREATE TABLE t_camera_storage
(
    uuid            varchar(64),
    raw_data        blob,
    player_name     varchar(20),
    player_position varchar(40),
    world_name      varchar(64),
    time            timestamp
);



insert into t_camera_storage
values (?, ?,?,?,?,?);



select uuid, raw_data from t_camera_storage where uuid = ?;
select raw_data from t_camera_storage where uuid = ?;



select 1 + 1;
select 'hello world';
select 1 + 1 as suma;


