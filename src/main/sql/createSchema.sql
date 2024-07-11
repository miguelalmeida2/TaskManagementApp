drop table if exists dbLS.students;
drop table if exists dbLS.courses;

create table dbLS.courses (
  cid serial primary key,
  name varchar(80)
);

create table dbLS.students (
  number int primary key,
  name varchar(80),
  course int references dbLS.courses(cid)
);