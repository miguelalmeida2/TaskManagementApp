insert into courses(name) values ('LEIC');
insert into students(course, number, name) values (1, 12345, 'Alice');
insert into students(course, number, name) select cid as course, 12346 as number, 'Bob' as name from courses where name = 'LEIC'



insert into users(number,name,password,token,email)
values (2,'miguel','41d823ab960ef49973ce628377d7bf5d','41d823ab960ef49973ce628377d7bf5d','miguel@mail.com'),
(3,'david','d6d78c3165f36a6585caf50953ace0d8','d6d78c3165f36a6585caf50953ace0d8','david@mail.com'),(4,'darlene','71d6c631e36a3432a5899bb09c51d52a','71d6c631e36a3432a5899bb09c51d52a','darlene@mail.com'),(5,'ricardo','41d823ab960ef49973ce628377d7bf5d','41d823ab960ef49973ce628377d7bf5d','ricardo@mail.com');

insert into board(name, description)
values ('Board LS','Board de LS');

insert into boards_users(board, users)
values ('Board LS',4);

insert into list(id, name, board)
values ('321','Lista TODO','Board LS');

insert into list(id, name, board)
values ('231','Lista DONE','Board LS');


--Ano Mes Dia
insert into card(id, cix,name, description,creation_date, conclusion_date,list)
values ('1', 1,'Backend', 'Backend Card','2023-05-07', '2023-05-09','321'),
('2', 2,'Frontend', 'Frontend Card','2023-05-06', '2023-05-15','321'),
('3', 3,'Auth', 'Auth Card','2023-05-05', '2023-05-25','321'),
('4', 4,'Logging', 'Logging Card','2023-05-04', '2023-05-31','321');
