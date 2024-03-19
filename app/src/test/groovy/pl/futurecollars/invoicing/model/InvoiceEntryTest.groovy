package pl.futurecollars.invoicing.model

import pl.futurecollars.invoicing.service.TestHelper
import spock.lang.Specification

class InvoiceEntryTest extends Specification {

    InvoiceEntry invoiceEntry = new InvoiceEntry(
            "Usługa nabycia oprogramowania",
            BigDecimal.valueOf(2500),
            BigDecimal.valueOf(575),
            Vat.VAT_23)

    def "should get invoice description"() {
        given:
        String description = "Usługa nabycia oprogramowania"

        when:
        String expResult = invoiceEntry.getDescription()

        then:
        description.equals(expResult)
    }

    def "should get price of invoice"() {
        given:
        BigDecimal currentPrice = BigDecimal.valueOf(2500)

        when:
        BigDecimal expResult = invoiceEntry.getPrice()

        then:
        currentPrice == expResult
    }

    def "should get value of tax"() {
        given:
        BigDecimal taxValue = BigDecimal.valueOf(575)

        when:
        BigDecimal expResult = invoiceEntry.getVatValue()

        then:
        taxValue == expResult

    }

    def "should get tax rate"() {
        given:
        Vat currentRate = Vat.VAT_23

        when:
        Vat expResult = invoiceEntry.getVatRate()

        then:
        currentRate == expResult
    }

    def "summary test for all methods available for InvoiceEntry"() {
        given:
        def entry1 = TestHelper.createInvoices()[0].getInvoiceEntry()
        def entry2 = TestHelper.createInvoices()[1].getInvoiceEntry()
        def entry3 = TestHelper.createInvoices()[2].getInvoiceEntry()

        when: "When equal() called on objects return true"
        entry1.equals(entry1)

        then: "Then hashCode compare should return true"
        entry1.hashCode() == (entry1).hashCode()

        when: "When equal() called on objects return false"
        !entry1.equals(entry2)
        !entry2.equals(entry3)
        !entry1.equals(entry3)

        then: "Then hashCode compare should return false"
        entry1.hashCode() != entry2.hashCode()
        entry2.hashCode() != entry3.hashCode()
        entry1.hashCode() != entry3.hashCode()

        when: "If toString method give correct values"
        def first = entry1.toString()
        def second = entry2.toString()

        then: "Then comparing also works correctly"
        first == first
        first != second

        when: "setDescription called should modify description"
        def description = "New description"
        entry1.setDescription(description)

        then: "getDescription should get modified object"
        entry1.getDescription() == description

        when: "setPrice called should modify price"
        def price = BigDecimal.valueOf(5000)
        entry2.setPrice(price)

        then: "getPrice should get modified price"
        price == entry2.getPrice()
        price.toString() == (entry2.getPrice().toString())

        when: "setVatValue called should modify Vat value"
        def vatValue = BigDecimal.valueOf(640)
        entry2.setVatValue(vatValue)

        then: "getVatValue called should get modified Vat value"
        vatValue == entry2.getVatValue()
        vatValue.toString() == (entry2.getVatValue().toString())

        when: "setVatRate called should modify Vat rate"
        def vatRate = Vat.VAT_0
        entry3.setVatRate(vatRate)

        then: "getVatRate called should get modified Vat rate"
        vatRate == entry3.getVatRate()
        vatRate.getRate() == entry3.getVatRate().getRate()
        vatRate.toString() == entry3.getVatRate().toString()
    }


}
