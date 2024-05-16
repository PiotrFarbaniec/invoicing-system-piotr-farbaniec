package pl.futurecollars.invoicing

import pl.futurecollars.invoicing.model.Car
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.model.Vat

import java.math.RoundingMode
import java.time.LocalDate

class TestHelper {

    static Invoice[] getInvoice() {
        def buyer = [
                Company.builder()
                        .id(1)
                        .taxIdentification("423-456-78-90")
                        .address("30-210 Krakow, ul.Kwiatowa 30")
                        .name("COMPLEX")
                        .build(),
                Company.builder()
                        .id(3)
                        .taxIdentification("106-008-18-19")
                        .address("62-500 Konin, ul.Akacjowa 2/15")
                        .name("Digiwave")
                        .build(),
                Company.builder()
                        .id(5)
                        .taxIdentification("689-456-56-65")
                        .address("22-455 Czartoria, ul.Powstancow 102")
                        .name("Pixelux")
                        .build(),
        ]

        def seller = [
                Company.builder()
                        .id(2)
                        .taxIdentification("538-321-55-32")
                        .address("04-413 Warszawa, ul.Gorna 5")
                        .name("GLOBAL SOLUTIONS")
                        .build(),
                Company.builder()
                        .id(4)
                        .taxIdentification("458-116-52-51")
                        .address("62-730 Czajkow, ul.Polna 56")
                        .name("Infotech")
                        .build(),
                Company.builder()
                        .id(6)
                        .taxIdentification("987-743-21-08")
                        .address("61-324 Poznan, ul.Wincentego Witosa 18")
                        .name("Energon")
                        .build(),
        ]

        def invoiceEntry = [
                InvoiceEntry.builder()
                        .id(1)
                        .description("Invoice description No1")
                        .quantity(5)
                        .netPrice(BigDecimal.valueOf(2500.00))
                        .vatValue(BigDecimal.valueOf(575.00))
                        .vatRate(Vat.VAT_23)
                        .build(),
                InvoiceEntry.builder()
                        .id(2)
                        .description("Invoice description No2")
                        .quantity(3)
                        .netPrice(BigDecimal.valueOf(3000.00))
                        .vatValue(BigDecimal.valueOf(240.00))
                        .vatRate(Vat.VAT_8)
                        .build(),
                InvoiceEntry.builder()
                        .id(3)
                        .description("Invoice description No3")
                        .quantity(1)
                        .netPrice(BigDecimal.valueOf(3500.00))
                        .vatValue(BigDecimal.valueOf(175.00))
                        .vatRate(Vat.VAT_5)
                        .build(),
                InvoiceEntry.builder()
                        .id(4)
                        .description("Invoice description No4")
                        .quantity(4)
                        .netPrice(BigDecimal.valueOf(1000.00))
                        .vatValue(BigDecimal.valueOf(230.00))
                        .vatRate(Vat.VAT_23)
                        .build(),
                InvoiceEntry.builder()
                        .id(5)
                        .description("Invoice description No5")
                        .quantity(2)
                        .netPrice(BigDecimal.valueOf(4600.00))
                        .vatValue(BigDecimal.valueOf(874.00))
                        .vatRate(Vat.VAT_19)
                        .build(),
                InvoiceEntry.builder()
                        .id(6)
                        .description("Invoice description No6")
                        .quantity(3)
                        .netPrice(BigDecimal.valueOf(3750.00))
                        .vatValue(BigDecimal.valueOf(300.00))
                        .vatRate(Vat.VAT_8)
                        .build(),
        ]

        def invoices = []

        invoices = List.of(
                Invoice.builder()
                        .id(1)
                        .number("INV_00001")
                        .date(LocalDate.now())
                        .buyer(buyer[0])
                        .seller(seller[0])
                        .entries(List.of(invoiceEntry[0], invoiceEntry[1]))
                        .build(),
                Invoice.builder()
                        .id(2)
                        .number("INV_00002")
                        .date(LocalDate.now())
                        .buyer(buyer[1])
                        .seller(seller[1])
                        .entries(List.of(invoiceEntry[2], invoiceEntry[3]))
                        .build(),
                Invoice.builder()
                        .id(3)
                        .number("INV_00003")
                        .date(LocalDate.now())
                        .buyer(buyer[2])
                        .seller(seller[2])
                        .entries(List.of(invoiceEntry[4], invoiceEntry[5]))
                        .build())
        return invoices
    }




    static Invoice[] getInvoiceForTaxCalculator() {
        def sellingCompany = Company.builder()
                .taxIdentification("500-400-30-20")
                .address("30-200 Krakow, ul.Warszawska 7")
                .name("SELLER S.A.")
                .build()
        def buyingCompany = Company.builder()
                .taxIdentification("100-200-30-40")
                .address("10-100 Warszawa, ul.Dluga 14")
                .name("BUYER S.C.")
                .build()

        def car = List.of(Car.builder()
                .isUsedPrivately(true)
                .registrationNumber("KK 37071")
                .build(),
                Car.builder()
                        .isUsedPrivately(false)
                        .registrationNumber("WN 11122")
                        .build())

        def invoiceEntries = [
                InvoiceEntry.builder()
                        .description("Sale of ten laptops")
                        .quantity(10)
                        .netPrice(BigDecimal.valueOf(10 * 5000.00))
                        .vatValue(BigDecimal.valueOf(10 * 5000.00 * Vat.VAT_23.rate).setScale(2, RoundingMode.FLOOR))
                        .vatRate(Vat.VAT_23)
                        .build(),
                InvoiceEntry.builder()
                        .description("Sales of documentation management software")
                        .quantity(10)
                        .netPrice(BigDecimal.valueOf(10 * 1049.00))
                        .vatValue(BigDecimal.valueOf(10 * 1049.00 * Vat.VAT_23.rate).setScale(2, RoundingMode.FLOOR))
                        .vatRate(Vat.VAT_23)
                        .build(),
                InvoiceEntry.builder()
                        .description("Purchase of office supplies")
                        .quantity(5)
                        .netPrice(BigDecimal.valueOf(5 * 546.00).setScale(2, RoundingMode.FLOOR))
                        .vatValue(BigDecimal.valueOf(5 * 546.00 * Vat.VAT_23.rate).setScale(2, RoundingMode.FLOOR))
                        .vatRate(Vat.VAT_23)
                        .build(),
                InvoiceEntry.builder()
                        .description("Servicing of the vehicle along with the purchase of parts")
                        .quantity(1)
                        .netPrice(BigDecimal.valueOf(3138.55))
                        .vatValue(BigDecimal.valueOf(1 * 3138.55 * Vat.VAT_23.rate).setScale(2, RoundingMode.FLOOR))
                        .vatRate(Vat.VAT_23)
                        .carRelatedExpenses(car[0])
                        .build(),
                InvoiceEntry.builder()
                        .description("Fuel purchase")
                        .quantity(1)
                        .netPrice(BigDecimal.valueOf(0.00))
                        .vatValue(BigDecimal.valueOf(1 * 0.00 * Vat.VAT_23.rate).setScale(2, RoundingMode.FLOOR))
                        .vatRate(Vat.VAT_23)
                        .carRelatedExpenses(car[1])
                        .build(),
        ]

        def invoices = [
                Invoice.builder()
                        .id(1)
                        .number("2022/10/12_FK111111")
                        .date(LocalDate.now())
                        .buyer(buyingCompany)
                        .seller(sellingCompany)
                        .entries(List.of(invoiceEntries[0], invoiceEntries[1]))
                        .build(),

                Invoice.builder()
                        .id(2)
                        .number("2022/05/05_TF222222")
                        .date(LocalDate.now())
                        .buyer(sellingCompany)
                        .seller(buyingCompany)
                        .entries(List.of(invoiceEntries[2], invoiceEntries[3], invoiceEntries[4]))
                        .build()
        ]
        return invoices
    }
}
