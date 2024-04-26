package pl.futurecollars.invoicing.model

import pl.futurecollars.invoicing.TestHelper
import spock.lang.Specification

class InvoiceEntryTest extends Specification {

    InvoiceEntry invoiceEntry = InvoiceEntry.builder()
            .description("Usługa nabycia oprogramowania")
            .quantity(2)
            .netPrice(BigDecimal.valueOf(2500))
            .vatValue(BigDecimal.valueOf(575))
            .vatRate(Vat.VAT_23)
            .build()

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
        BigDecimal expResult = invoiceEntry.getNetPrice()

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
        def entry = TestHelper.getInvoice()[0].getEntries()

        when:
        entry.equals(entry)

        then:
        entry.hashCode() == (entry).hashCode()
    }

    def "When equal() called on different objects should return false"() {
        given:
        def entry1 = TestHelper.getInvoice()[0].getEntries()
        def entry2 = TestHelper.getInvoice()[1].getEntries()
        def entry3 = TestHelper.getInvoice()[2].getEntries()

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
        def entry1 = TestHelper.getInvoice()[0].getEntries()
        def entry2 = TestHelper.getInvoice()[1].getEntries()

        when:
        def first = entry1.toString()
        def second = entry2.toString()

        then:
        first == first
        first != second
    }

    def "setDescription called should modify description"() {
        given:
        def entry = TestHelper.getInvoice()[0].getEntries()[0]

        when:
        def description = "New description"
        entry.setDescription(description)

        then:
        entry.getDescription() == description
    }

    def "setPrice called should modify price"() {
        given:
        def entry = TestHelper.getInvoice()[1].getEntries()[0]

        when:
        def price = BigDecimal.valueOf(5000)
        entry.setNetPrice(price)

        then:
        price == entry.getNetPrice()
        price.toString() == (entry.getNetPrice().toString())
    }

    def "setVatValue called should modify Vat value"() {
        given:
        def entry = TestHelper.getInvoice()[1].getEntries()[0]

        when:
        def vatValue = BigDecimal.valueOf(640)
        entry.setVatValue(vatValue)

        then:
        vatValue == entry.getVatValue()
        vatValue.toString() == (entry.getVatValue().toString())
    }

    def "setVatRate called should modify Vat rate"() {
        given:
        def entry = TestHelper.getInvoice()[2].getEntries()[0]

        when:
        def vatRate = Vat.VAT_0
        entry.setVatRate(vatRate)

        then:
        vatRate == entry.getVatRate()
        vatRate.getRate() == entry.getVatRate().getRate()
        vatRate.toString() == entry.getVatRate().toString()
    }
}
