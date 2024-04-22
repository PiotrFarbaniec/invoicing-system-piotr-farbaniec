package pl.futurecollars.invoicing

import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.model.Vat

import java.time.LocalDate

class TestHelper {

    static Invoice[] getInvoice() {
        def buyer = [
                Company.builder()
                        .taxIdentification("423-456-78-90")
                        .address("30-210 Krakow, ul.Kwiatowa 30")
                        .name("COMPLEX")
                        .build(),
                Company.builder()
                        .taxIdentification("106-008-18-19")
                        .address("62-500 Konin, ul.Akacjowa 2/15")
                        .name("Digiwave")
                        .build(),
                Company.builder()
                        .taxIdentification("689-456-56-65")
                        .address("22-455 Czartoria, ul.Powstancow 102")
                        .name("Pixelux")
                        .build(),
        ]

        def seller = [
                Company.builder()
                        .taxIdentification("538-321-55-32")
                        .address("04-413 Warszawa, ul.Gorna 5")
                        .name("GLOBAL SOLUTIONS")
                        .build(),
                Company.builder()
                        .taxIdentification("458-116-52-51")
                        .address("62-730 Czajkow, ul.Polna 56")
                        .name("Infotech")
                        .build(),
                Company.builder()
                        .taxIdentification("987-743-21-08")
                        .address("61-324 Poznan, ul.Wincentego Witosa 18")
                        .name("Energon")
                        .build(),
        ]

        def invoiceEntry = [
                InvoiceEntry.builder()
                        .description("Invoice description No1")
                        .quantity(1)
                        .netPrice(BigDecimal.valueOf(2500))
                        .vatValue(BigDecimal.valueOf(575))
                        .vatRate(Vat.VAT_23)
                        .build(),
                InvoiceEntry.builder()
                        .description("Invoice description No2")
                        .quantity(1)
                        .netPrice(BigDecimal.valueOf(3000))
                        .vatValue(BigDecimal.valueOf(240))
                        .vatRate(Vat.VAT_8)
                        .build(),
                InvoiceEntry.builder()
                        .description("Invoice description No3")
                        .quantity(1)
                        .netPrice(BigDecimal.valueOf(3500))
                        .vatValue(BigDecimal.valueOf(175))
                        .vatRate(Vat.VAT_5)
                        .build(),
        ]

        def invoices = []

        for (int i = 0; i < buyer.size(); i++) {
            invoices << Invoice.builder()
                    .id(i + 1)
                    .date(LocalDate.now())
                    .buyer(buyer[i])
                    .seller(seller[i])
                    .entries(List.of(invoiceEntry[i]))
                    .build()
        }
        return invoices
    }
}
