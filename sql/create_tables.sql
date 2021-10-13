create table access_token (
    token varchar(255) primary key,
    group_name varchar(255),
    num_case_studies int,
    valid_until timestamp
);

create table case_study (
    number int primary key,
    title varchar(255)
);

insert into access_token values ('0000', 'Test Gruppe', 3, NOW() + '30 days'::interval);
insert into access_token values ('1111', 'Test Gruppe 2', 2, NOW() + '30 days'::interval);


insert into case_study values (1, 'Einzelhandel'),
                              (2, 'Paketversand'),
                              (3, 'Hotelreservierung'),
                              (4, 'Ticketverkauf'),
                              (5, 'Pflegedienst');
