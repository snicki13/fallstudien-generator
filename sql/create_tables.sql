create table student_group (
    group_id int primary key,
    group_name varchar(255),
    token varchar(255),
    num_case_studies int,
    num_exclusions int,
    valid_until timestamp
);

create table case_study (
    number int primary key,
    title varchar(255)
);

create table result (
    result_id int primary key,
    case_study int,
    group_id int,
    foreign key(case_study) references case_study(number),
    foreign key(group_id) references student_group(group_id)
);

insert into student_group values (1, 'Test Gruppe','0000', 3, 3, NOW() + '30 days'::interval);
insert into student_group values (2, 'Test Gruppe 2', '1111', 2, 2, NOW() + '30 days'::interval);


insert into case_study values (1, 'Einzelhandel'),
                              (2, 'Paketversand'),
                              (3, 'Hotelreservierung'),
                              (4, 'Ticketverkauf'),
                              (5, 'Pflegedienst');
