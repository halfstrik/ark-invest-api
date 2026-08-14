#!/bin/sh
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-SQL
    CREATE DATABASE ark_invest_db;
    GRANT ALL PRIVILEGES ON DATABASE ark_invest_db TO $POSTGRES_USER;
SQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname ark_invest_db -f /docker-entrypoint-initdb.d/create_tables.sql
