package pl.futurecollars.invoicing.service

import pl.futurecollars.invoicing.TestHelper
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

class InvoiceServiceIntegrationTest extends Specification{

    def "all methods should be compatible with InvoiceService class"() {
        given: "An instance of InvoiceService with InMemoryDatabase"
        InMemoryDatabase memoryDatabase = new InMemoryDatabase()
        InvoiceService invoiceService = new InvoiceService(memoryDatabase)
        Invoice invoice1 = TestHelper.createInvoices()[0]
        Invoice invoice2 = TestHelper.createInvoices()[1]

        when: "When invoice1 is saved in Database"
        Integer savedInvoice1 = invoiceService.save(invoice1)
        Integer savedInvoice2 = invoiceService.save(invoice2)

        then: "Then saved invoice1 is not null"
        savedInvoice1 != null
        savedInvoice2 != null

        when: "When the invoice1 is found by ID"
        Invoice found = invoiceService.getById(savedInvoice1).get()

        then: "Then the invoice1 found is equal to the stored in memory"
        assert found.id == savedInvoice1

        when: "When invoice1 is updated"
        found.getSeller().setTaxIdentification("987-743-21-08")
        invoiceService.update(found.getId(), found)

        then: "Then has new provided parameter"
        Invoice updatedInvoice = invoiceService.getById(found.getId()).get()
        assert updatedInvoice.getSeller().getTaxIdentification() == "987-743-21-08"

        when: "When invoice1 is deleted"
        invoiceService.delete(updatedInvoice.getId())

        then: "Then invoice1 is permanently removed from the database"
        invoiceService.getById(updatedInvoice.getId()).isEmpty()

        when: "When database contain any invoices"
        Invoice stored = invoiceService.getById(savedInvoice2).get()
        List<Invoice> storedInvoices = invoiceService.getAll()

        then: "Then return the list of all invoices"
        storedInvoices.size() != 0
        storedInvoices.containsAll(stored)
    }
}
