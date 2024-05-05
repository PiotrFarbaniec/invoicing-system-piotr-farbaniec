CREATE TABLE public.invoice_entry
(
    id                      bigserial                       NOT NULL,
    description             character varying(200)          NOT NULL,
    quantity                numeric(5)                      NOT NULL DEFAULT 1,
    net_price               numeric(10, 2)                  NOT NULL,
    vat_value               numeric(10, 2)                  NOT NULL,
    vat_rate                bigint                          NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE public.invoice_entry
    ADD CONSTRAINT vat_rate_fk FOREIGN KEY (vat_rate)
    REFERENCES public.vat (id);

ALTER TABLE IF EXISTS public.invoice_entry
    OWNER to postgres;