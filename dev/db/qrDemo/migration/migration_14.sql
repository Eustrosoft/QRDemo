-- Добавление таблиц qrdemo m_range, gs_label, p_code

CREATE TABLE IF NOT EXISTS qrdemo.m_range
(
--    ZRID    bigint      NOT NULL, -- 1
--    ZVER    bigint      NOT NULL, -- 1
--    ZTOV    bigint      NOT NULL, -- 0
--    ZLVL    smallint    NOT NULL, -- 31
--    ZPID    bigint      NOT NULL, -- 0
--    ZSTA    char(1)     NOT NULL, -- 'N', 'C', 'D'
    rstart            BIGINT,
    rbitl             SMALLINT,
    rtype             VARCHAR(8),
    status            CHAR(1),
    action            VARCHAR(16),
    redirect          VARCHAR(127),
    alloc             TIMESTAMP WITH TIME ZONE,
    member_id         BIGINT,
    doc_id            BIGINT,
    owiki             VARCHAR(65535),
    PRIMARY KEY (id)
) INHERITS (entity);

CREATE TABLE IF NOT EXISTS qrdemo.gs_label
(
    qr_id           BIGINT,
    gtin            BIGINT,
    rtype           VARCHAR(8),
    key             VARCHAR(8),
    value           VARCHAR(64),
    tail            VARCHAR(1024),
    comment         VARCHAR(2048),
    PRIMARY KEY (id)
) INHERITS (entity);

CREATE TABLE IF NOT EXISTS qrdemo.p_code
(
    doc_id          BIGINT,
    row_id          BIGINT,
    participant_id  BIGINT,
    h_fields        CHAR(1),
    h_files         CHAR(1),
    p               VARCHAR(64),
    p2              VARCHAR(64),
    p2_mode         VARCHAR(8),
    p2_prompt       VARCHAR(1024),
    comment         VARCHAR(1024),
    PRIMARY KEY (doc_id, row_id)
);

ALTER TABLE qrdemo.dictionary ADD COLUMN "language_code" VARCHAR(16);
ALTER TABLE qrdemo.form_file ADD COLUMN "order" INT;
ALTER TABLE qrdemo.qr_file ADD COLUMN "order" INT;

CREATE OR REPLACE FUNCTION get_migration_ver() returns int as 'select 14' LANGUAGE SQL SECURITY INVOKER;
