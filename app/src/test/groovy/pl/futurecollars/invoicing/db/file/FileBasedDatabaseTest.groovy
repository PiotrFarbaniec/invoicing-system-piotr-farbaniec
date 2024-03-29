package pl.futurecollars.invoicing.db.file

import pl.futurecollars.invoicing.TestHelper
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.utils.FileManager
import pl.futurecollars.invoicing.utils.FileService
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class FileBasedDatabaseTest extends Specification {

    def provider = new PathProvider("INVOICE_TEST.txt", "ID_TEST.txt")
    def manager = new FileManager(provider)
    def fileService = new FileService()
    def jsonService = new JsonService()
    def idService = new IdService(fileService, jsonService, provider)
    Path idPath = provider.idPath
    Path invoicePath = provider.invoicePath

    FileBasedDatabase fileDatabase = new FileBasedDatabase (manager, fileService, jsonService, idService, provider)

    Invoice invoice1 = TestHelper.createInvoices()[0]
    Invoice invoice2 = TestHelper.createInvoices()[1]
    Invoice invoice3 = TestHelper.createInvoices()[2]


    def setup() {
        Files.deleteIfExists(Path.of("ID_TEST.txt"))
        Files.deleteIfExists(Path.of("INVOICE_TEST.txt"))
        Files.deleteIfExists(Path.of("[INVOICE_TEST.txt]_BACKUP.txt"))
    }

    def "save() method called should successfully save an invoices in file"() {
        setup:
        Files.createFile(invoicePath)
        Files.createFile(idPath)

        when:
        def nextId1 = fileDatabase.save(invoice1)

        then:
        fileDatabase.getAll().size() == 1
        nextId1 == 2

        when:
        def nextId2 = fileDatabase.save(invoice2)

        then:
        fileDatabase.getAll().size() == 2
        nextId2 == 3

        when:
        def nextId3 = fileDatabase.save(invoice3)

        then:
        fileDatabase.getAll().size() == 3
        nextId3 == 4

        cleanup:
        Files.deleteIfExists(invoicePath)
        Files.deleteIfExists(idPath)
    }

    def "getByID() called should throw an exception when file not exists"() {
        when:
        !invoicePath.toFile().exists()
        !idPath.toFile().exists()
        fileDatabase.getById(1)

        then:
        thrown(RuntimeException.class)
    }

    def "getByID() should return empty when record with id not exists"() {
        setup:
        Files.createFile(invoicePath)
        Files.createFile(idPath)
        fileDatabase.save(invoice1)
        fileDatabase.save(invoice2)
        fileDatabase.save(invoice3)

        when:
        def result = fileDatabase.getById(5)

        then:
        result.isEmpty()
        !result.isPresent()

        cleanup:
        Files.deleteIfExists(invoicePath)
        Files.deleteIfExists(idPath)
    }

    def "getByID() should return invoice when record with id exists"() {
        setup:
        Files.createFile(invoicePath)
        Files.createFile(idPath)
        fileDatabase.save(invoice1)
        fileDatabase.save(invoice2)
        fileDatabase.save(invoice3)
        fileDatabase.save(invoice3)

        when:
        def invoice = fileDatabase.getById(3)

        then:
        !invoice.isEmpty()
        invoice.isPresent()

        cleanup:
        Files.deleteIfExists(invoicePath)
        Files.deleteIfExists(idPath)
    }

    def "getAll() should return all stored invoices"() {
        setup:
        Files.createFile(invoicePath)
        Files.createFile(idPath)
        fileDatabase.save(invoice1)
        fileDatabase.save(invoice1)
        fileDatabase.save(invoice1)

        when:
        def invoicesList = fileDatabase.getAll()

        then:
        invoicesList.size() == 3

        cleanup:
        Files.deleteIfExists(invoicePath)
        Files.deleteIfExists(idPath)
        Files.deleteIfExists(Path.of(String.format("[%s]_BACKUP.txt", invoicePath.getFileName())))
    }

    def "getAll() should throw an exception when file not exists"() {

        when:
        fileDatabase.getAll()

        then:
        thrown(RuntimeException.class)
    }

    def "update() should update successfully when invoice with this id exists"() {
        setup:
        Files.createFile(invoicePath)
        Files.createFile(idPath)
        fileDatabase.save(invoice1)
        fileDatabase.save(invoice1)
        fileDatabase.save(invoice1)
        def updatedInvoice = invoice3
        def updatedId = 1

        when:
        fileDatabase.update(updatedId, updatedInvoice)
        def invoicesList = fileDatabase.getAll()

        then:
        invoicesList.size() == 3
        invoicesList.get(0) == updatedInvoice
        invoicesList.get(0).id == updatedId

        cleanup:
        Files.deleteIfExists(invoicePath)
        Files.deleteIfExists(idPath)
        Files.deleteIfExists(Path.of(String.format("[%s]_BACKUP.txt", invoicePath.getFileName())))
    }

    def "update() should not update if invoice not exists"() {
        setup:
        Files.createFile(invoicePath)
        Files.createFile(idPath)
        fileDatabase.save(invoice1)
        fileDatabase.save(invoice2)
        fileDatabase.save(invoice3)
        def updatedInvoice = invoice3
        def updatedId = 8

        when:
        fileDatabase.update(updatedId, updatedInvoice)
        def invoicesList = fileDatabase.getAll()

        then:
        invoicesList.size() == 3
        invoicesList.get(0) == fileDatabase.getById(1).get()
        invoicesList.get(1) == fileDatabase.getById(2).get()
        invoicesList.get(2) == fileDatabase.getById(3).get()

        invoicesList.get(0).id == 1
        invoicesList.get(1).id == 2
        invoicesList.get(2).id == 3

        cleanup:
        Files.deleteIfExists(invoicePath)
        Files.deleteIfExists(idPath)
        Files.deleteIfExists(Path.of(String.format("[%s]_BACKUP.txt", invoicePath.getFileName())))
    }

    def "update() should throw an exception when file not exists"() {
        given:
        def updatedId = 3
        def updatedInvoice = invoice3

        when:
        fileDatabase.update(updatedId, updatedInvoice)

        then:
        thrown(RuntimeException.class)
    }

    def "delete() should throw an exception when file not exists"() {
        given:
        def deletedId = 3

        when:
        fileDatabase.delete(deletedId)

        then:
        thrown(RuntimeException.class)
    }

    def "delete() should remove an invoice with required id"() {
        setup:
        Files.createFile(invoicePath)
        Files.createFile(idPath)
        fileDatabase.save(invoice1)
        fileDatabase.save(invoice1)
        fileDatabase.save(invoice1)

        when:
        fileDatabase.delete(2)

        then:
        fileDatabase.getAll().size() == 2
        fileDatabase.getById(1).present
        !fileDatabase.getById(2).present
        fileDatabase.getById(3).present

        cleanup:
        Files.deleteIfExists(invoicePath)
        Files.deleteIfExists(Path.of(String.format("[%s]_BACKUP.txt", invoicePath.getFileName())))
        Files.deleteIfExists(idPath)
    }

    def "delete() should do nothing if invoice not exists"() {
        setup:
        Files.createFile(invoicePath)
        Files.createFile(idPath)
        fileDatabase.save(invoice1)
        fileDatabase.save(invoice1)
        fileDatabase.save(invoice1)

        when:
        fileDatabase.delete(7)

        then:
        fileDatabase.getAll().size() == 3
        fileDatabase.getById(1).present
        fileDatabase.getById(2).present
        fileDatabase.getById(3).present

        cleanup:
        Files.deleteIfExists(invoicePath)
        Files.deleteIfExists(Path.of(String.format("[%s]_BACKUP.txt", invoicePath.getFileName())))
        Files.deleteIfExists(idPath)
    }

}
