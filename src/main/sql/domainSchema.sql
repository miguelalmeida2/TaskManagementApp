BEGIN TRANSACTION;

create table if not exists users (
    number serial primary key,
    name varchar(80) not null,
    password varchar(80) not null,
    token varchar(80) not null,
    email varchar(80) unique not null
);

create table if not exists board (
    name varchar(80) primary key,
    description varchar(200) not null
);

create table if not exists boards_users (
    board varchar(80) references board(name),
    users int references users(number)
);

create table if not exists list (
	id varchar(36) primary key,
	name varchar(80) not null,
    board varchar(80) references board(name)
);

create table if not exists card (
    id varchar(36) primary key,
    cix int not null,
    name varchar(80) not null,
    description varchar(200) not null,
    creation_date date not null,
    conclusion_date date not null,
    archived boolean,
    list varchar(36) references list(id)
);
COMMIT TRANSACTION;
