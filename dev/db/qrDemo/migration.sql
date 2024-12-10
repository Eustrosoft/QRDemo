alter table participant add settings varchar(2048);

alter table form_field add data varchar(2048);

create table if not exists qr_file
(
    name        varchar(256) not null,
    filename    varchar(256) not null,
    extension   varchar(64),
    active
    active boolean     not null default true,
    primary key (id)
) INHERITS (entity);


