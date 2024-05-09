INSERT INTO public.car (registration_number, is_used_privately)
VALUES  ('KK 37071', true),
        ('WN 45862', false),
        ('WW 68284', false);
ALTER TABLE IF EXISTS public.car
    OWNER to postgres;