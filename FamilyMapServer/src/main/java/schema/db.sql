drop table if exists users;
drop table if exists persons;
drop table if exists events;
drop table if exists authtokens;


CREATE TABLE users (
username TEXT NOT NULL,
password TEXT NOT NULL,
email TEXT NOT NULL,
firstName TEXT NOT NULL,
lastName TEXT NOT NULL,
gender TEXT NOT NULL,
personID TEXT NOT NULL,
PRIMARY KEY(username),
);

CREATE TABLE persons (
personID TEXT NOT NULL,
associatedUsername TEXT NOT NULL,
firstName TEXT NOT NULL,
lastName TEXT NOT NULL,
gender TEXT NOT NULL,
fatherID TEXT,
motherID TEXT,
spouseID TEXT,
PRIMARY KEY(personID),
);

CREATE TABLE events (
eventID TEXT NOT NULL,
associatedUsername TEXT NOT NULL,
personID TEXT NOT NULL,
latitude NUMERIC NOT NULL,
longitude NUMERIC NOT NULL,
country TEXT NOT NULL,
city TEXT NOT NULL,
eventType TEXT NOT NULL,
year INTEGER NOT NULL,
PRIMARY KEY(eventID),

);

CREATE TABLE authtokens (
username TEXT NOT NULL,
token TEXT NOT NULL,
PRIMARY KEY(token)

);


