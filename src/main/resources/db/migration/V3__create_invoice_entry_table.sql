CREATE TABLE public.invoice_entry
(
    id                      bigserial                       NOT NULL,
    "description"           character varying(200)          NOT NULL,
    "quantity"              numeric(5)                      NOT NULL DEFAULT 0,
    "net_price"             numeric(10, 2)                  NOT NULL DEFAULT 0.00,
    "vat_value"             numeric(10, 2)                  NOT NULL DEFAULT 0.00,
    "vat_rate"              numeric(10, 2)                  NOT NULL DEFAULT 0.00,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.invoice_entry
    OWNER to postgres;