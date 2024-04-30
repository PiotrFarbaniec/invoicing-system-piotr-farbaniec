CREATE TABLE public.car
(
    id                      bigserial                       NOT NULL,
    "registration_number"   character varying(50)           NOT NULL,
    "is_used_privately"     boolean                         NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.car
    OWNER to postgres;