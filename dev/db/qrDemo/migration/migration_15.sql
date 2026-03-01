-- Добавление p_code_seq для генерации

CREATE SEQUENCE p_code_seq
START WITH 1
INCREMENT BY 1;

CREATE OR REPLACE FUNCTION get_migration_ver() returns int as 'select 15' LANGUAGE SQL SECURITY INVOKER;
