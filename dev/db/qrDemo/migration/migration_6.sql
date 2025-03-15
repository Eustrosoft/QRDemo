set schema 'qrdemo';

-- ADD EDIT TYPE Into dictionary
INSERT INTO qrdemo.dictionary (name, code, value, description) values ('JUMP_QRSVC', 'INPUT_TYPE', 'EDIT', 'Редактировать');

-- alert table column size
ALTER TABLE qrdemo.file ALTER COLUMN storage_path TYPE varchar(2048);

-- alert size of redirect attribute in qr talbe
ALTER TABLE qrdemo.qr ALTER COLUMN redirect TYPE varchar(2048);

-- ADD EDIT TYPE Into dictionary
INSERT INTO qrdemo.dictionary (name, code, value, description)
values ('STANDARD', 'QR_ACTION', 'STANDARD', 'Стандарт'),
    ('REDIRECT', 'QR_ACTION', 'REDIRECT', 'Перенаправление'),
    ('QRSVC', 'QR_ACTION', 'QRSVC', 'Перенаправление с параметром');
