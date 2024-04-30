ALTER TABLE public.invoices
    ADD COLUMN buyer bigint NOT NULL;

ALTER TABLE public.invoices
    ADD CONSTRAINT buyer_fk FOREIGN KEY (buyer)
    REFERENCES public.company (id);


ALTER TABLE public.invoices
    ADD COLUMN seller bigint NOT NULL;

ALTER TABLE public.invoices
    ADD CONSTRAINT seller_fk FOREIGN KEY (seller)
    REFERENCES public.company (id);


ALTER TABLE public.invoices
    ADD COLUMN invoice_entry bigint NOT NULL;

ALTER TABLE public.invoices
    ADD CONSTRAINT invoice_entry_fk FOREIGN KEY (invoice_entry)
    REFERENCES public.invoice_entry (id);
