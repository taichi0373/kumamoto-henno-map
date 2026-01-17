CREATE TABLE community_bus (
  route_id varchar(21) NOT NULL,
  agency_id varchar(16) DEFAULT NULL,
  origin_agency_id BIGINT DEFAULT NULL,
  business_name varchar(31) DEFAULT NULL,
  trip_name varchar(39) DEFAULT NULL
);

ALTER TABLE community_bus ADD CONSTRAINT community_bus_pkey PRIMARY KEY (route_id);
