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

ALTER TABLE public.users
RENAME COLUMN provider_id TO uid;