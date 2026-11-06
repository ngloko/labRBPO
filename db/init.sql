--     -v owner_password='пароль_владельца' \
--     -v app_password='пароль_приложения' \
--     -f db/init.sql
--
-- Значения должны совпадать с DB_INIT_PASSWORD и DB_PASSWORD в .env.
-- museum_owner — владелец схемы: выполняет schema.sql/data.sql.
-- museum_app   — пользователь приложения: только DML, без DDL.

\set ON_ERROR_STOP on

\if :{?owner_password}
\else
    \warn 'Не задан owner_password. Пример: psql -U postgres -v owner_password=... -v app_password=... -f db/init.sql'
    \quit
\endif

\if :{?app_password}
\else
    \warn 'Не задан app_password. Пример: psql -U postgres -v owner_password=... -v app_password=... -f db/init.sql'
    \quit
\endif

-- Роли создаются, только если их ещё нет: повторный запуск безопасен.
SELECT format('CREATE ROLE museum_owner LOGIN PASSWORD %L NOSUPERUSER NOCREATEDB NOCREATEROLE',
              :'owner_password')
WHERE NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'museum_owner')
\gexec

SELECT format('CREATE ROLE museum_app LOGIN PASSWORD %L NOSUPERUSER NOCREATEDB NOCREATEROLE',
              :'app_password')
WHERE NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'museum_app')
\gexec

SELECT 'CREATE DATABASE museum OWNER museum_owner ENCODING ''UTF8'''
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'museum')
\gexec

\connect museum

REVOKE ALL ON SCHEMA public FROM PUBLIC;
GRANT USAGE, CREATE ON SCHEMA public TO museum_owner;
GRANT CONNECT ON DATABASE museum TO museum_app;
GRANT USAGE ON SCHEMA public TO museum_app;

ALTER DEFAULT PRIVILEGES FOR ROLE museum_owner IN SCHEMA public
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO museum_app;
ALTER DEFAULT PRIVILEGES FOR ROLE museum_owner IN SCHEMA public
    GRANT USAGE, SELECT ON SEQUENCES TO museum_app;
