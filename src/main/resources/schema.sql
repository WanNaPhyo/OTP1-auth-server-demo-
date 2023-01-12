create table if not exists user(
    username varchar(45) unique,
    password text null,
    primary key (username)
);

create table if not exists otp(
    username varchar(45) not null,
    code varchar(45) null,
    primary key (username)
);