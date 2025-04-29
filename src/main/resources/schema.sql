CREATE TABLE IF NOT EXISTS public.member
(
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR
);


ALTER TABLE public.member
    ADD IF NOT EXISTS role VARCHAR;

