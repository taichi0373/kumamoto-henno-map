CREATE TABLE store_condition (
  id varchar(6) NOT NULL,
  store_benefit_id INTEGER DEFAULT NULL,
  address varchar(10) DEFAULT NULL,
  license_status varchar(8) DEFAULT NULL,
  min_age varchar(2) DEFAULT NULL,
  max_age varchar(10) DEFAULT NULL,
  note varchar(71) DEFAULT NULL
);

ALTER TABLE store_condition ADD CONSTRAINT store_condition_pkey PRIMARY KEY (id);
