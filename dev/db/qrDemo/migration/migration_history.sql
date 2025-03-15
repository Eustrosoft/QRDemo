set schema 'qrdemo';

CREATE TABLE qrdemo.h_entity (
    zsta character(1),
    zdato timestamp without time zone,
    id bigint NOT NULL,
    participant_id bigint,
    type character varying(16) NOT NULL,
    created timestamp without time zone NOT NULL,
    updated timestamp without time zone NOT NULL,
    name character varying(128),
    description character varying(512)
);

CREATE TABLE qrdemo.h_qr (
    code bigint NOT NULL,
    form_id bigint,
    data character varying(65536),
    action character varying(16),
    redirect character varying(2048)
) INHERITS (qrdemo.h_entity);

CREATE FUNCTION qrdemo.do_h_qr() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        --
        -- Добавление строки в emp_audit, которая отражает операцию, выполняемую в emp;
        -- для определения типа операции применяется специальная переменная TG_OP.
        --
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO qrdemo.h_qr SELECT 'D', now(), OLD.*;
            RETURN OLD;
        ELSIF (TG_OP = 'UPDATE') THEN
            IF (OLD = NEW) THEN
             RETURN NEW;
            END IF;
            INSERT INTO qrdemo.h_qr SELECT 'C', now(), OLD.*;
            RETURN NEW;
        END IF;
        RETURN NULL; -- возвращаемое значение для триггера AFTER игнорируется
    END;
$$;
ALTER FUNCTION qrdemo.do_h_qr() OWNER TO qrdemo;

CREATE OR REPLACE TRIGGER qrdemo_qr_audit_trig AFTER INSERT OR DELETE OR UPDATE ON qrdemo.qr FOR EACH ROW EXECUTE FUNCTION qrdemo.do_h_qr();

