package pl.futurecollars.invoicing.db.memory

import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.TestHelper
import spock.lang.Specification

class InMemoryDatabaseTest extends Specification {

    InMemoryDatabase database = new InMemoryDatabase()
    Invoice invoice1 = TestHelper.getInvoice()[0]
    Invoice invoice2 = TestHelper.getInvoice()[1]
    Invoice invoice3 = TestHelper.getInvoice()[2]

    def "should save given invoices"() {
        given:

        when:
        database.save(invoice1)
        database.save(invoice2)

        then:
        assert database.getAll().size() == 2
        assert database.getById(1).get() == invoice1
        assert database.getById(2).get() == invoice2
    }

    def "when getById() is called should return invoice if present"() {
        given:
        database.save(invoice1)
        database.save(invoice2)
        database.save(invoice3)

        when:
        Optional<Invoice> expResult1 = database.getById(2)
        Optional<Invoice> expResult2 = database.getById(4)

        then:
        assert expResult1.isPresent()
        assert expResult1.get() == invoice2
        assert !expResult2.isPresent()
    }

    def "update() should modify existing invoice"() {
        given:
        database.save(invoice1)
        database.save(invoice2)

        when:
        Invoice updatedInvoice = invoice3
        database.update(2, updatedInvoice)

        then:
        assert database.getById(2).get() == updatedInvoice
    }

    def "When no such invoice update() should terminate without action"() {
        given:
        database.save(invoice1)
        database.save(invoice2)

        when:
        Invoice updatedInvoice = invoice3
        database.update(3, updatedInvoice)

        then:
        assert database.getById(3).isEmpty()
        assert database.getAll().size() == 2
    }

    def "delete() called should remove invoice if exisits"() {
        given:
        database.save(invoice1)
        database.save(invoice2)
        database.save(invoice3)

        when:
        database.delete(2)
        List<Invoice> invoiceList = database.getAll()

        then:
        invoiceList.size() == 2

        and:
        invoiceList.containsAll([invoice1, invoice3])
    }

    def "When no such invoice delete() should terminate without action"() {
        given:
        database.save(invoice1)
        database.save(invoice2)
        def before = database.getAll()

        when:
        database.delete(3)
        def after = database.getAll()

        then:
        before == after
    }

    def "when getAll() called should return list of all invoices"() {
        given:
        database.save(invoice1)
        database.save(invoice2)
        database.save(invoice3)

        when:
        List<Invoice> expList = database.getAll()

        then:
        assert expList.size() == 3
        assert expList.containsAll([invoice1, invoice2, invoice3])
    }
}
