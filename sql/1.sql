CREATE TABLE public.users (
	id serial NOT NULL,
	email varchar NOT NULL,
	password_hashed varchar NOT NULL,
	fullname varchar NOT NULL,
	dob date NOT NULL,
	gender varchar(1) NOT NULL,
	photo_profile bytea NULL,
	city varchar NULL,
	about_me varchar(200) NULL,
	interest varchar(200) NULL,
	CONSTRAINT users_pkey PRIMARY KEY (id)
);