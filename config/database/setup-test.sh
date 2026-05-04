#!/bin/bash
set -e

# テスト用DB（benefit_map_test）を作成し、setup.sqlを適用する
# docker-entrypoint-initdb.d により初回コンテナ起動時に自動実行される

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE benefit_map_test;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "benefit_map_test" \
    -f /docker-entrypoint-initdb.d/setup.sql
