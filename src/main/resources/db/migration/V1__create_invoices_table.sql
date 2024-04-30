CREATE TABLE public.invoices
(
    id          bigserial               NOT NULL,
    "number"    character varying(50)   NOT NULL,
    "date"      date                    NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.invoices
    OWNER to postgres;