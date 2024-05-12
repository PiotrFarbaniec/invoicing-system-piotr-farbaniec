CREATE TABLE public.vat
(
    id                      bigserial                       NOT NULL,
    name                    character varying(20)           NOT NULL,
    vat_rate                numeric(4, 3)                   NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO public.vat (name, vat_rate)
VALUES  ('VAT_23',   0.230),
        ('VAT_19',   0.190),
        ('VAT_9',    0.090),
        ('VAT_8',    0.080),
        ('VAT_7_75', 0.775),
        ('VAT_5',    0.050),
        ('VAT_0',    0.000);

--ALTER TABLE IF EXISTS public.vat
--    OWNER to postgres;