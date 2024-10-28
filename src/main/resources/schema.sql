create table if not exists student
(
    id serial primary key,
    name text not null
);

create table if not exists course
(
    id serial primary key,
    name text not null,
    "limit" integer not null
);

create table if not exists enrollment
(
    id serial primary key,
    course_id integer not null,
    student_id integer not null,
    order_id integer not null,
    status char(1) not null
);

create table if not exists notification
(
    id serial primary key,
    course_id integer not null,
    student_id integer not null,
    status char(1) not null
);
-- fixed id's used in test - let's start with 10
SELECT setval('enrollment_id_seq', 10);