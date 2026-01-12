CREATE TABLE municipality (
  id varchar(6) NOT NULL,
  name varchar(10) DEFAULT NULL,
  name_kana varchar(50) DEFAULT NULL
);

ALTER TABLE municipality ADD CONSTRAINT municipality_pkey PRIMARY KEY (id);
