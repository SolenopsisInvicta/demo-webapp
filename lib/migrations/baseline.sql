-- Schema
DROP DATABASE IF EXISTS webappdemo;
CREATE SCHEMA webappdemo;
-- Tables
CREATE TABLE webappdemo.users(
	id int AUTO_INCREMENT PRIMARY KEY,
	username varchar(255) NOT NULL,
	pw_hash varchar(255) NOT NULL,
	email varchar(255),
	member_since date,
	UNIQUE INDEX idx_username (username)
);