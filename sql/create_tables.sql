create table student_group (
    group_id SERIAL primary key,
    group_name varchar(255),
    token varchar(255) unique,
    num_case_studies int,
    num_exclusions int default 3,
    valid_until timestamp default NOW() + '7 days'::interval,
    teacher int default 1
);

create table case_study (
    number int primary key,
    title varchar(255)
);

create table exclusions (
    case_study int primary key,
    times_excluded int
);

create table teacher (
    teacher_id int,
    teacher_name varchar(60),
    teacher_email varchar(60)
);

insert into exclusions(case_study, times_excluded) values (1, 2),
                                                         (2, 2),
                                                         (3, 1),
                                                         (4, 3),
                                                         (5, 9),
                                                         (6, 5),
                                                         (7, 5),
                                                         (8, 7),
                                                         (9, 11),
                                                         (10, 3);



create table study_result (
    result_id SERIAL primary key,
    case_study int,
    group_id int,
    foreign key(case_study) references case_study(number),
    foreign key(group_id) references student_group(group_id)
);

insert into student_group(group_name, token, num_case_studies) values ('Test Gruppe','0000', 3),
                                                                      ('Test Gruppe 2', '1111', 2);

insert into case_study values (1, 'Einzelhandel'),
                              (2, 'Paketversand'),
                              (3, 'Hotelreservierung'),
                              (4, 'Ticketverkauf'),
                              (5, 'Pflegedienst');
