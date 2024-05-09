INSERT INTO public.company (tax_identification, address, name, pension_insurance, health_insurance)
VALUES  ('423-456-78-90', '30-210 Krakow, ul.Kwiatowa 30', 'COMPLEX', 508.38, 317.28),
        ('106-008-18-19', '62-500 Konin, ul.Akacjowa 2/15', 'Digiwave', 691.42, 412.53),
        ('689-456-56-65', '22-455 Czartoria, ul.Powstancow 102', 'Pixelux', 489.78, 376.12),
        ('538-321-55-32', '04-413 Warszawa, ul.Gorna 5', 'GLOBAL SOLUTIONS', 774.44, 645.94),
        ('458-116-52-51', '62-730 Czajkow, ul.Polna 56', 'Infotech', 712.56, 602.03),
        ('987-743-21-08', '61-324 Poznan, ul.Wincentego Witosa 18', 'Energon', 804.00, 740.06);
ALTER TABLE IF EXISTS public.company
    OWNER to postgres;