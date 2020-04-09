CREATE TABLE public.users (
	id serial NOT NULL,
	email varchar NOT NULL,
	password_hashed varchar NOT NULL,
	created_by varchar NOT NULL,
	created_date date NOT NULL,
	modified_by varchar NOT NULL,
	modified_date date NOT NULL,
	CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE table public.profiles (
	id serial NOT NULL,
	user_pk int4 NOT NULL REFERENCES users(id),
	fullname varchar NOT NULL,
	dob date NOT NULL,
	gender varchar(1) NOT NULL,
	photo_profile bytea NULL,
	city varchar NULL,
	about_me varchar(200) NULL,
	interest varchar(200) NULL,
	CONSTRAINT profiles_pkey PRIMARY KEY (id)
);

CREATE TABLE public.config (
	id serial NOT NULL,
	config_key varchar NOT NULL,
	config_value varchar NOT NULL,
	created_by varchar NOT NULL,
	created_date date NOT NULL,
	modified_by varchar NOT NULL,
	modified_date date NOT NULL,
	CONSTRAINT config_pkey PRIMARY KEY (id)
);