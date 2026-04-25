CREATE TABLE store_benefit (
  id INTEGER NOT NULL,
  name varchar(29) DEFAULT NULL,
  detail varchar(109) DEFAULT NULL,
  tel_number varchar(12) DEFAULT NULL,
  location_fetched SMALLINT DEFAULT NULL
);

ALTER TABLE store_benefit ADD CONSTRAINT store_benefit_pkey PRIMARY KEY (id);
