set schema 'qrdemo';

CREATE TABLE if NOT EXISTS tariff (
    id              BIGSERIAL       NOT NULL UNIQUE,
    name            VARCHAR(64)     NOT NULL,
    description     VARCHAR(1024)    DEFAULT NULL,
    version         INT             NOT NULL DEFAULT 1,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE limits_seq (

    participant_id       BIGINT         NOT NULL,
    tariff_id            BIGINT         NULL,
    type                 VARCHAR(64)    NOT_NULL,
    current              INT            NOT_NULL DEFAULT 0,
    max                  INT            NOT_NULL DEFAULT 16,
    max_size             INT            DEFAULT NULL,
    valid_from           TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    valid_until          TIMESTAMP      DEFAULT NULL,
    assigned_at          TIMESTAMP      DEFAULT NULL,
    created              TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated              TIMESTAMP      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

--CREATE TABLE if not exists qrdemo.registration_request (
--        username      varchar(64) not null unique,
--        password      varchar(128) not null,
--        email         varchar(128) not null unique,
--        referer       bigint,
--        lei           varchar(256),
--        address       varchar(128),
--        site          varchar(512),
--        organization  varchar(512),
--        active        boolean     not null default true,
--        status        varchar(16) not null default 'start'
--        ip_address    varchar(64),
--        client_name   varchar(128),
--
--        primary key (id)
--)  INHERITS (entity);