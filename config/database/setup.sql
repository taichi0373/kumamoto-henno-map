-- 1. DDL
\i '/docker-entrypoint-initdb.d/DDL/TABLE/SPRING_SESSION.SQL';
\i '/docker-entrypoint-initdb.d/DDL/TABLE/MUNICIPALITY.SQL';
\i '/docker-entrypoint-initdb.d/DDL/TABLE/COMMUNITY_BUS.SQL';
\i '/docker-entrypoint-initdb.d/DDL/TABLE/USERS.SQL';
\i '/docker-entrypoint-initdb.d/DDL/TABLE/GTFS_BENEFITS.SQL';
\i '/docker-entrypoint-initdb.d/DDL/TABLE/STORE_BENEFIT.SQL';
\i '/docker-entrypoint-initdb.d/DDL/TABLE/STORE_CONDITION.SQL';
\i '/docker-entrypoint-initdb.d/DDL/TABLE/BENEFITS.SQL';
\i '/docker-entrypoint-initdb.d/DDL/TABLE/CONDITIONS.SQL';

-- 2. DML
\i '/docker-entrypoint-initdb.d/DML/TABLE/MUNICIPALITY.SQL';
\i '/docker-entrypoint-initdb.d/DML/TABLE/COMMUNITY_BUS.SQL';
\i '/docker-entrypoint-initdb.d/DML/TABLE/GTFS_BENEFITS.SQL';
\i '/docker-entrypoint-initdb.d/DML/TABLE/STORE_BENEFIT.SQL';
\i '/docker-entrypoint-initdb.d/DML/TABLE/STORE_CONDITION.SQL';
\i '/docker-entrypoint-initdb.d/DML/TABLE/BENEFITS.SQL';
\i '/docker-entrypoint-initdb.d/DML/TABLE/CONDITIONS.SQL';
