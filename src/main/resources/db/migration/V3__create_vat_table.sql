CREATE TABLE public.vat
(
    id                      bigserial                       NOT NULL,
    name                    character varying(20)           NOT NULL,
    vat_rate                numeric(4, 3)                   NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO public.vat (name, vat_rate)
VALUES  ('VAT 23',   0.230),
        ('VAT 19',   0.190),
        ('VAT 9',    0.090),
        ('VAT 8',    0.080),
        ('VAT 7.75', 0.775),
        ('VAT 5',    0.050),
        ('VAT 0',    0.000);

ALTER TABLE IF EXISTS public.vat
    OWNER to postgres;