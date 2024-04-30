CREATE TABLE public.invoice_connected_to_entries
(
    invoices_id         bigint      NOT NULL,
    invoice_entry_id    bigint      NOT NULL,
    PRIMARY KEY (invoices_id, invoice_entry_id)
);

ALTER TABLE public.invoice_connected_to_entries
    ADD CONSTRAINT invoices_id_fk FOREIGN KEY (invoices_id)
    REFERENCES public.invoices (id);

ALTER TABLE public.invoice_connected_to_entries
    ADD CONSTRAINT invoice_entry_id_fk FOREIGN KEY (invoice_entry_id)
    REFERENCES public.invoice_entry (id);

ALTER TABLE IF EXISTS public.invoice_connected_to_entries
    OWNER to postgres;