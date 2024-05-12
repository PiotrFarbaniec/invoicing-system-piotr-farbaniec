CREATE TABLE public.car
(
    id                      bigserial                       NOT NULL,
    registration_number     character varying(20)           NOT NULL,
    is_used_privately       boolean                         NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);

--ALTER TABLE IF EXISTS public.car
--    OWNER to postgres;