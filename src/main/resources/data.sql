delete from student;
insert into student(id, name) values (1, 'Adam') ;
insert into student(id, name) values (2, 'Barbara');
insert into student(id, name) values (3, 'C');
insert into student(id, name) values (4, 'Donald');
insert into student(id, name) values (5, 'Estera');

delete from course;
insert into course(id, name, "limit") values (100, 'Math', 2) ;
insert into course(id, name, "limit") values (101, 'Physics', 1) ;

delete from enrollment;
insert into enrollment(course_id, student_id, order_id, status)
values (100, 1, 1, 'A') ;
-- insert into enrollment(course_id, student_id, order_id, status)
-- values (101, 2, 1, 'A') ;
-- insert into enrollment(course_id, student_id, order_id, status)
-- values (101, 3, 2, 'R') ;

delete from notification;