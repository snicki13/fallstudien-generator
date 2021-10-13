create table valid_tokens (
    token varchar(255) primary key,
    group_name varchar(255),
    count_case_studies int,
    until timestamp
);

create table case_studies (
    number int primary key,
    title varchar(255)
);

insert into valid_tokens values ('0000', 'Test Gruppe', 3, NOW() + '30 days'::interval);
insert into valid_tokens values ('1111', 'Test Gruppe 2', 2, NOW() + '30 days'::interval);


insert into case_studies values (1, 'Einzelhandel'),
                                (2, 'Paketversand'),
                                (3, 'Hotelbuchung');
