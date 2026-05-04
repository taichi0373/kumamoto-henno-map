CREATE TABLE conditions (
  id SERIAL PRIMARY KEY,
  benefit_id INTEGER DEFAULT NULL,
  license_status varchar(10) DEFAULT NULL,
  min_age varchar(2) DEFAULT NULL,
  max_age varchar(2) DEFAULT NULL,
  address varchar(6) DEFAULT NULL,
  note text DEFAULT NULL
);

CREATE INDEX idx_conditions_benefit_id ON conditions (benefit_id);
CREATE INDEX idx_conditions_address ON conditions (address);

ALTER TABLE conditions
  ADD CONSTRAINT conditions_ibfk_1 FOREIGN KEY (benefit_id) REFERENCES benefits (id) ON DELETE CASCADE,
  ADD CONSTRAINT conditions_ibfk_2 FOREIGN KEY (address) REFERENCES municipality (id) ON DELETE CASCADE;
