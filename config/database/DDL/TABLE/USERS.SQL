CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  username varchar(50) NOT NULL,
  password varchar(255) NOT NULL,
  birth_date date NOT NULL,
  address varchar(6) NOT NULL,
  license_status varchar(50) NOT NULL
);
