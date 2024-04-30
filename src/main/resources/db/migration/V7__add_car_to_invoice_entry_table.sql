ALTER TABLE public.invoice_entry
    ADD COLUMN car_related_expenses bigint;

ALTER TABLE public.invoice_entry
    ADD CONSTRAINT car_related_expenses_fk FOREIGN KEY (car_related_expenses)
    REFERENCES public.car (id);