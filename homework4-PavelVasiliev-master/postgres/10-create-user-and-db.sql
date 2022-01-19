-- file: 10-create-user-and-db.sql
CREATE DATABASE rss;
CREATE ROLE postgres WITH PASSWORD 'admin';
GRANT ALL PRIVILEGES ON DATABASE rss TO postgres;
ALTER ROLE postgres WITH LOGIN;