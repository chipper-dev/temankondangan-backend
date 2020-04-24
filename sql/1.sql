CREATE TABLE public.users (
	user_id int8 PRIMARY KEY,
	email varchar NOT NULL,
	password_hashed varchar,
	created_by varchar NOT NULL,
	created_date timestamp NOT NULL,
	modified_by varchar NOT NULL,
	modified_date timestamp NOT NULL,
    provider varchar NOT NULL,
    provider_id varchar
);

CREATE table public.profile (
	profile_id int8 PRIMARY KEY,
	user_id int8 NOT NULL REFERENCES users(user_id),
	full_name varchar NOT NULL,
	dob date NOT NULL,
	gender varchar(1) NOT NULL,
	photo_profile oid NULL,
	city varchar NULL,
	about_me varchar(200) NULL,
	interest varchar(200) NULL,
	created_by varchar NOT NULL,
    created_date timestamp NOT NULL,
    modified_by varchar NOT NULL,
    modified_date timestamp NOT NULL
);

CREATE TABLE public.config (
	config_id int8 PRIMARY KEY,
	config_key varchar NOT NULL,
	config_value varchar NOT NULL,
	created_by varchar NOT NULL,
	created_date timestamp NOT NULL,
	modified_by varchar NOT NULL,
	modified_date timestamp NOT NULL
);

ALTER TABLE public.users
RENAME COLUMN modified_by TO last_modified_by;

ALTER TABLE public.users
RENAME COLUMN modified_date TO last_modified_date;

ALTER TABLE public.profile
RENAME COLUMN modified_by TO last_modified_by;

ALTER TABLE public.profile
RENAME COLUMN modified_date TO last_modified_date;

ALTER TABLE public.config
RENAME COLUMN modified_by TO last_modified_by;

ALTER TABLE public.config
RENAME COLUMN modified_date TO last_modified_date;

ALTER TABLE users
ADD COLUMN data_state VARCHAR NOT NULL DEFAULT 'ACTIVE';

ALTER TABLE users 
ALTER COLUMN data_state 
DROP DEFAULT;

ALTER TABLE public.users
ADD COLUMN logout timestamp NULL;

CREATE TABLE public.event
(
    event_id int8 PRIMARY KEY,
	user_id int8 NOT NULL REFERENCES users(user_id),
    additional_info varchar(300) NOT NULL,
    companion_gender varchar(1) NOT NULL,
    date_and_time timestamp without time zone NOT NULL,
    maximum_age integer NOT NULL,
    minimum_age integer NOT NULL,
    title varchar(50),
	created_by varchar NOT NULL,
	created_date timestamp NOT NULL,
	last_modified_by varchar NOT NULL,
	last_modified_date timestamp NOT NULL
)

ALTER TABLE public.users
RENAME COLUMN provider_id TO uid;