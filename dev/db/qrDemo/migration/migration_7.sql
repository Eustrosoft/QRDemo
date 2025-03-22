set schema 'qrdemo';

INSERT INTO qrdemo.dictionary(
	name, code, value, description)
	VALUES ('CHUNK_SIZE', 'FILE_UPLOAD', '1048576', 'Chunk file size for chunks file upload');

CREATE TABLE file_blob (
        ZOID    bigint NOT NULL, -- id
        ZRID    bigint NOT NULL, -- 1
        ZVER    bigint NOT NULL, -- 1
        ZTOV    bigint NOT NULL, -- 0 - actual, zver + 1 - archive
        ZSID    bigint NOT NULL, -- participant_id
        ZLVL    smallint NOT NULL, -- 31
        ZPID    bigint NOT NULL, -- 0
-- Added 18.03.2025
        ZUID    bigint NOT NULL, -- participant_id
        ZSTA    "char" NOT NULL, -- 'N', 'C', 'D'
        ZDATE   timestamptz NOT NULL, -- Created date
        ZDATO   timestamptz NULL, -- Changed date
        ZUIDO   bigint NOT NULL, -- User deleted id
--
        chunk   bytea NULL,
        no      bigint NULL,
        size    bigint NULL,
        crc32   bigint NULL,
        PRIMARY KEY (ZOID, ZRID, ZVER)
-- PRIMARY KEY (ZOID,ZRID,ZVER)
);

CREATE OR REPLACE FUNCTION get_migration_ver() returns int as 'select 7' LANGUAGE SQL SECURITY INVOKER;