package pl.futurecollars.invoicing.model

import pl.futurecollars.invoicing.TestHelper
import spock.lang.Specification

import java.time.LocalDate

class InvoiceTest extends Specification {

    Invoice invoice = TestHelper.createInvoices()[0]

    def "should set new company id"() {
        given:
        Integer odlId = 1
        Integer newId = 20

        when:
        invoice.setId(newId)
        int expResult = invoice.getId()

        then:
        newId == expResult
    }

    def "should set new invoice date"() {
        given:
        LocalDate oldDate = invoice.getDate()
        LocalDate newDate = LocalDate.of(2020, 06, 25)

        when:
        invoice.setDate(newDate)
        LocalDate expResult = invoice.getDate()

        then:
        newDate.equals(expResult)

        and:
        !oldDate.equals(expResult)
    }

    def "should set new buyer"() {
        given:
        Company oldBuyer = invoice.getBuyer()
        Company newBuyer = new Company("689-456-56-65", "22-455 Czartoria, ul.Powstańców 102", "Pixelux")

        when:
        invoice.setBuyer(newBuyer)
        Company expResult = invoice.getBuyer()

        then:
        !oldBuyer.equals(expResult)

        and:
        newBuyer.equals(expResult)
    }

    def "should set new seller"() {
        given:
        Company oldSeller = invoice.getSeller()
        Company newSeller = new Company("987-743-21-08", "61-324 Poznań, ul.Wincentego Witosa 18", "Energon")

        when:
        invoice.setSeller(newSeller)
        Company expResult = invoice.getSeller()

        then:
        newSeller.equals(expResult)

        and:
        !oldSeller.equals(expResult)

    }
}
