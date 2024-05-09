INSERT INTO public.invoice_connected_to_entries (invoice_id, invoice_entry_id)
VALUES  (1, 1),
        (1, 2),
        (1, 3),
        (2, 3),
        (2, 4),
        (2, 5),
        (3, 3),
        (3, 4),
        (4, 1),
        (4, 2),
        (4, 5),
        (5, 2),
        (5, 3),
        (5, 5),
        (6, 1),
        (6, 3),
        (6, 4),
        (6, 5);
ALTER TABLE IF EXISTS public.invoice_connected_to_entries
    OWNER to postgres;