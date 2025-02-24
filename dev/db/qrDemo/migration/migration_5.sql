set schema 'qrdemo';

CREATE TABLE if not exists registration_request (
        username      varchar(64) not null unique,
        password      varchar(128) not null,
        email         varchar(128) not null unique,
        referer       bigint,
        lei           varchar(256),
        address       varchar(128),
        site          varchar(512),
        organization  varchar(512),
        active        boolean     not null default true,
        status        varchar(16) not null default 'start'
        ip_address    varchar(64),
        client_name   varchar(128),

        primary key (id)
)  INHERITS (entity);