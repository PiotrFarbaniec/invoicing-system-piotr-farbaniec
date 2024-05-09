INSERT INTO public.invoices (number, "date", buyer, seller, invoice_entry)
VALUES  ('2022/10/12_FK300200', '2022-10-12', 1, 3, 1),
        ('2022/05/05_TF458000', '2022-05-05', 1, 4, 2),
        ('2020/04/19_GA002223', '2020-04-19', 2, 4, 3),
        ('2020/08/17_FF554400', '2020-08-17', 2, 5, 4),
        ('2024/01/23_GA991145', '2024-01-23', 3, 5, 5),
        ('2024/05/03_FQ110099', '2024-05-03', 3, 6, 6);
ALTER TABLE IF EXISTS public.invoices
    OWNER to postgres;


--INSERT INTO public.invoices (number, "date", buyer, seller, invoice_entry)
--VALUES  ('2022/10/12_FK300200', '2022-10-12', 1, 3, 1),
--        ('2022/05/05_TF458000', '2022-05-05', 1, 4, 2),
--        ('2020/04/19_GA002223', '2020-04-19', 2, 4, 3),
--        ('2020/08/17_FF554400', '2020-08-17', 2, 5, 4),
--        ('2024/01/23_GA991145', '2024-01-23', 3, 5, 5),
--        ('2024/05/03_FQ110099', '2024-05-03', 3, 6, 5);
--ALTER TABLE IF EXISTS public.invoice_entry
--    OWNER to postgres;