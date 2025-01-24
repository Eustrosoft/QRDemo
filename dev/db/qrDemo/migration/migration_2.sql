ALTER TABLE form_field ADD caption VARCHAR(256);
ALTER TABLE form_field ALTER COLUMN placeholder TYPE VARCHAR(1024);
ALTER TABLE form_field ADD PRIMARY KEY (form_id, name);
