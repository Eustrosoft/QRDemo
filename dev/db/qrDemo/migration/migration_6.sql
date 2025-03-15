set schema 'qrdemo';

-- ADD EDIT TYPE Into dictionary
INSERT INTO qrdemo.dictionary (name, code, value, description) values ('JUMP_QRSVC', 'INPUT_TYPE', 'EDIT', 'Редактировать');

-- alert table column size
ALTER TABLE qrdemo.file ALTER COLUMN storage_path TYPE varchar(2048);

-- alert size of redirect attribute in qr table
ALTER TABLE qrdemo.qr ALTER COLUMN redirect TYPE varchar(2048);

-- add types of qr code/qr range in dictionary
INSERT INTO qrdemo.dictionary (name, code, value, description)
values ('STANDARD', 'QR_ACTION', 'STANDARD', 'Стандарт'),
    ('REDIRECT', 'QR_ACTION', 'REDIRECT', 'Перенаправление'),
    ('QRSVC', 'QR_ACTION', 'QRSVC', 'Перенаправление с параметром');
