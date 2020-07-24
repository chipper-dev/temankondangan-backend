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
ALTER COLUMN data_state DROP DEFAULT;

ALTER TABLE public.users
ADD COLUMN logout timestamp NULL;

ALTER TABLE public.users
RENAME COLUMN provider_id TO uid;

ALTER TABLE public.profile ALTER COLUMN about_me TYPE varchar(200);
ALTER TABLE public.profile ALTER COLUMN interest TYPE varchar(200);

ALTER TABLE event
ADD COLUMN city VARCHAR NOT NULL DEFAULT 'Yogyakarta';

ALTER TABLE event
ALTER COLUMN city DROP DEFAULT;

ALTER TABLE event
ADD COLUMN data_state VARCHAR NOT NULL DEFAULT 'ACTIVE';

ALTER TABLE event
ALTER COLUMN data_state DROP DEFAULT;

ALTER TABLE profile
ADD COLUMN data_state VARCHAR NOT NULL DEFAULT 'ACTIVE';

ALTER TABLE profile
ALTER COLUMN data_state DROP DEFAULT;

ALTER TABLE event
RENAME date_and_time TO start_date_time;

ALTER TABLE event 
ADD finish_date_time timestamp without time zone NULL;

ALTER TABLE profile
ADD COLUMN photo_profile_filename VARCHAR;

ALTER TABLE users
ADD COLUMN messaging_token VARCHAR;

ALTER TABLE event
ADD COLUMN cancelled BOOLEAN;

ALTER TABLE ratings
ADD COLUMN user_voter_id int8;

ALTER TABLE applicants
	ADD COLUMN created_by varchar NOT NULL DEFAULT 'user',
    ADD COLUMN created_date timestamp NOT NULL DEFAULT NOW(),
    ADD COLUMN last_modified_by varchar NOT NULL DEFAULT 'user',
    ADD COLUMN last_modified_date timestamp NOT NULL DEFAULT NOW();

ALTER TABLE applicants
	ALTER COLUMN created_by DROP DEFAULT,
    ALTER COLUMN created_date DROP DEFAULT,
    ALTER COLUMN last_modified_by DROP DEFAULT,
    ALTER COLUMN last_modified_date DROP DEFAULT;

ALTER TABLE notifications
    ADD COLUMN data varchar(512);

ALTER TABLE chat
    ADD COLUMN content_type varchar;

ALTER TABLE chat
    DROP COLUMN created_by,
    DROP COLUMN last_modified_by,
    DROP COLUMN last_modified_date;