CREATE TABLE benefits (
  id SERIAL PRIMARY KEY,
  municipality varchar(6) DEFAULT NULL,
  category varchar(15) DEFAULT NULL,
  name varchar(30) DEFAULT NULL,
  detail varchar(50) DEFAULT NULL,
  expiration text DEFAULT NULL,
  tel_number varchar(50) DEFAULT NULL,
  url varchar(255) DEFAULT NULL
);

CREATE INDEX idx_benefits_municipality ON benefits (municipality);

ALTER TABLE benefits
  ADD CONSTRAINT benefits_ibfk_1 FOREIGN KEY (municipality) REFERENCES municipality (id) ON DELETE CASCADE;
