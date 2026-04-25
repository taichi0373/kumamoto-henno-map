CREATE TABLE gtfs_benefits (
  id SERIAL PRIMARY KEY,
  benefit_id INTEGER DEFAULT NULL,
  business_type varchar(6) DEFAULT NULL,
  business_id varchar(16) DEFAULT NULL,
  discount_type varchar(10) DEFAULT NULL,
  discount_value INTEGER DEFAULT NULL
);

CREATE INDEX idx_gtfs_benefits_benefit_id ON gtfs_benefits (benefit_id);
