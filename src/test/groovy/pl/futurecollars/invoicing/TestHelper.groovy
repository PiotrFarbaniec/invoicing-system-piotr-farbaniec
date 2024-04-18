package pl.futurecollars.invoicing

import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.model.Vat

import java.time.LocalDate

class TestHelper {
    static Invoice[] createInvoices() {
        def buyer = [
                new Company("423-456-78-90", "30-210 Krakow, ul.Kwiatowa 30", "COMPLEX"),
                new Company("106-008-18-19", "62-500 Konin, ul.Akacjowa 2/15", "Digiwave"),
                new Company("689-456-56-65", "22-455 Czartoria, ul.Powstancow 102", "Pixelux")
        ]
        def seller = [
                new Company("538-321-55-32", "04-413 Warszawa, ul.Gorna 5", "GLOBAL SOLUTIONS"),
                new Company("458-116-52-51", "62-730 Czajkow, ul.Polna 56", "Infotech"),
                new Company("987-743-21-08", "61-324 Poznan, ul.Wincentego Witosa 18", "Energon")
        ]
        def invoiceEntry = [
                List.of(new InvoiceEntry("Invoice description No1", 1, BigDecimal.valueOf(2500), BigDecimal.valueOf(575), Vat.VAT_23)),
                List.of(new InvoiceEntry("Invoice description No2", 1,  BigDecimal.valueOf(3000), BigDecimal.valueOf(240), Vat.VAT_8)),
                List.of(new InvoiceEntry("Invoice description No3", 1, BigDecimal.valueOf(3500), BigDecimal.valueOf(175), Vat.VAT_5))
        ]
        def invoices = []
        for (int i = 0; i < buyer.size(); i++) {
            invoices << new Invoice(i+1, LocalDate.now(), buyer[i], seller[i], invoiceEntry[i])
        }
        return invoices
    }
}
