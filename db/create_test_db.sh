#!/bin/sh
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-SQL
    CREATE DATABASE ark_invest_db;
    GRANT ALL PRIVILEGES ON DATABASE ark_invest_db TO $POSTGRES_USER;
SQL

cat /tmp/create_tables.sql /tmp/pre_seed.sql > /tmp/init.sql
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname ark_invest_db -f /tmp/init.sql
