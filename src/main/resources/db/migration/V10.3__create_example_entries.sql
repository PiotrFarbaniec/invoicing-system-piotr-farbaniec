INSERT INTO public.invoice_entry (description, quantity, net_price, vat_value, vat_rate, car_related_expenses)
VALUES  ('Sale of ten laptops', 10, 10*5000, 10*5000*0.23, 1, null),
        ('Sales of documentation management software', 10, 10*1049, 10*1049*0.23, 1, null),
        ('Purchase of office supplies', 5, 5*546, 5*546*0.080, 4, null),
        ('Servicing of the vehicle along with the purchase of parts', 1, 3138.55, 3138.55*0.23, 1, 1),
        ('Purchase of fuel', 1, 1078.49, 1078.49*0.23, 1, 1),
        ('Car windscreen replacement', 1, 2300.39, 2300.39*0.23, 1, 1);
ALTER TABLE IF EXISTS public.invoice_entry
    OWNER to postgres;