package pl.futurecollars.invoicing.model

import pl.futurecollars.invoicing.TestHelper
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

    def "When equal() called on same objects should return true"() {
        given:
        def entry = TestHelper.createInvoices()[0].getInvoiceEntry()

        when:
        entry.equals(entry)

        then:
        entry.hashCode() == (entry).hashCode()
    }

    def "When equal() called on different objects should return false"() {
        given:
        def entry1 = TestHelper.createInvoices()[0].getInvoiceEntry()
        def entry2 = TestHelper.createInvoices()[1].getInvoiceEntry()
        def entry3 = TestHelper.createInvoices()[2].getInvoiceEntry()

        when:
        !entry1.equals(entry2)
        !entry2.equals(entry3)
        !entry1.equals(entry3)

        then:
        entry1.hashCode() != entry2.hashCode()
        entry2.hashCode() != entry3.hashCode()
        entry1.hashCode() != entry3.hashCode()
    }

    def "toString method give correct values"() {
        given:
        def entry1 = TestHelper.createInvoices()[0].getInvoiceEntry()
        def entry2 = TestHelper.createInvoices()[1].getInvoiceEntry()

        when:
        def first = entry1.toString()
        def second = entry2.toString()

        then:
        first == first
        first != second
    }

    def "setDescription called should modify description"() {
        given:
        def entry = TestHelper.createInvoices()[0].getInvoiceEntry()

        when:
        def description = "New description"
        entry.setDescription(description)

        then:
        entry.getDescription() == description
    }

    def "setPrice called should modify price"() {
        given:
        def entry = TestHelper.createInvoices()[1].getInvoiceEntry()

        when:
        def price = BigDecimal.valueOf(5000)
        entry.setPrice(price)

        then:
        price == entry.getPrice()
        price.toString() == (entry.getPrice().toString())
    }

    def "setVatValue called should modify Vat value"() {
        given:
        def entry = TestHelper.createInvoices()[1].getInvoiceEntry()

        when:
        def vatValue = BigDecimal.valueOf(640)
        entry.setVatValue(vatValue)

        then:
        vatValue == entry.getVatValue()
        vatValue.toString() == (entry.getVatValue().toString())
    }

    def "setVatRate called should modify Vat rate"() {
        given:
        def entry = TestHelper.createInvoices()[2].getInvoiceEntry()

        when:
        def vatRate = Vat.VAT_0
        entry.setVatRate(vatRate)

        then:
        vatRate == entry.getVatRate()
        vatRate.getRate() == entry.getVatRate().getRate()
        vatRate.toString() == entry.getVatRate().toString()
    }
}
