package pl.futurecollars.invoicing.service

import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

class InvoiceServiceTest extends Specification {

    def database = Mock(Database)
    def service = new InvoiceService(database)
    def invoice = TestHelper.createInvoices()[0]

    def "should save given invoice"() {
        given:
        def invoice = TestHelper.createInvoices()[0]

        when:
        service.save(invoice)

        then:
        1 * database.save(invoice)
    }

    def "should get invoice by given id"() {
        given:
        //def id = 1
        def invoice = TestHelper.createInvoices()[0]
        service.save(invoice)

        when:
        service.getById(invoice.getId())

        then:
        1 * database.getById(invoice.getId())
    }

    def "should get list of all saved invoices"() {
        given:
        database.getAll() >> []

        when:
        service.getAll()

        then:
        1 * database.getAll()
    }

    def "should update invoice if present"() {
        given:
        def invoice = TestHelper.createInvoices()[0]
        def newInvoice = TestHelper.createInvoices()[2]
        database.update(invoice.getId(), newInvoice) >> newInvoice

        when:
        service.update(invoice.getId(), newInvoice)

        then:
        1 * database.update(invoice.id, newInvoice)
    }

    /*def "should throw an exception if updated invoice not exists"() {
        given:
        def id = 2
        def updateInvoice = TestHelper.createInvoices()[1]

        when:
        service.update(id, updateInvoice)

        then:
        thrown(Exception)
        1 * database.update(id, updateInvoice) >> {throw new Exception()}
    }*/


    def "should delete an invoice if present"() {
        given:
        def id = 1
        def invoice = TestHelper.createInvoices()[0]
        service.save(invoice)
        database.delete(invoice.id) >> []

        when:
        service.delete(id)

        then:
        1 * database.delete(id)
    }

/*    def "should throw an exception if deleted invoice not exists"() {
        given:
        def id = 1
        Invoice invoice = TestHelper.createInvoices()[0]

        when:
        service.save(invoice)
        service.delete(id)

        then:
        thrown(Exception)
        1 * database.delete(id) >> {throw new Exception()}
    }*/
}
