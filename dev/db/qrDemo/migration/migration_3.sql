ALTER TABLE participant ALTER COLUMN email DROP NOT NULL;
INSERT INTO dictionary(name, code, value, description)
values
('INPUT_URL', 'INPUT_TYPE', 'URL', 'Ссылка'),
('INPUT_PHONE_NUMBER', 'INPUT_TYPE', 'PHONE', 'Номер телефона'),
('INPUT_EMAIL', 'INPUT_TYPE', 'EMAIL', 'Email');
