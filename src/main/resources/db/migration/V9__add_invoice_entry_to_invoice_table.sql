ALTER TABLE public.invoices
    ADD COLUMN invoice_entry bigint;

ALTER TABLE public.invoices
    ADD CONSTRAINT invoice_entry_fk FOREIGN KEY (invoice_entry)
    REFERENCES public.invoices (id);
