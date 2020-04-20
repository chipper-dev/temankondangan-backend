CREATE TABLE public.users (
	user_id int8 PRIMARY KEY,
	email varchar NOT NULL,
	password_hashed varchar,
	created_by varchar NOT NULL,
	created_date timestamp NOT NULL,
	last_modified_by varchar NOT NULL,
	last_modified_date timestamp NOT NULL,
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
    last_modified_by varchar NOT NULL,
    last_modified_date timestamp NOT NULL
);

CREATE TABLE public.config (
	config_id int8 PRIMARY KEY,
	config_key varchar NOT NULL,
	config_value varchar NOT NULL,
	created_by varchar NOT NULL,
	created_date timestamp NOT NULL,
	last_modified_by varchar NOT NULL,
	last_modified_date timestamp NOT NULL
);