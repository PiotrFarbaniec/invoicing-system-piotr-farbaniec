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

    Path idPath = Path.of("ID_TEST.txt")
    Path invoicePath = Path.of("INVOICE_TEST.txt")
    def manager = new FileManager()
    def fileService = new FileService()
    def jsonService = new JsonService()
    def idService = new IdService(fileService, idPath)

    FileBasedDatabase fileDatabase = new FileBasedDatabase(manager, fileService, jsonService, idService, invoicePath, Invoice.class)

    Invoice invoice1 = TestHelper.getInvoice()[0]
    Invoice invoice2 = TestHelper.getInvoice()[1]
    Invoice invoice3 = TestHelper.getInvoice()[2]


    def setup() {
        Files.deleteIfExists(Path.of("ID_TEST.txt"))
        Files.deleteIfExists(Path.of("INVOICE_TEST.txt"))
        Files.deleteIfExists(Path.of("[INVOICE_TEST.txt]_BACKUP.txt"))
    }

    def "toString() should return String value of file paths"() {
        when:
        def invoiceFilePath = invoicePath.toString()
        def idFilePath = idPath.toString()

        then:
        invoiceFilePath == "INVOICE_TEST.txt"
        idFilePath == "ID_TEST.txt"
    }

    def "save() method called should successfully save an invoices in file"() {
        setup:
        Files.createFile(invoicePath)
        Files.createFile(idPath)

        when:
        def nextId1 = fileDatabase.save(invoice1)

        then:
        fileDatabase.getAll().size() == 1
        nextId1 == 1

        when:
        def nextId2 = fileDatabase.save(invoice2)

        then:
        fileDatabase.getAll().size() == 2
        nextId2 == 2

        when:
        def nextId3 = fileDatabase.save(invoice3)

        then:
        fileDatabase.getAll().size() == 3
        nextId3 == 3

        cleanup:
        Files.deleteIfExists(invoicePath)
        Files.deleteIfExists(idPath)
    }

    def "save() should create a new file while saving if not exists"() {
        when:
        !invoicePath.toFile().exists()
        fileDatabase.save(invoice1)

        then:
        invoicePath.toFile().exists()
        fileDatabase.getAll().size() == 1

        cleanup:
        Files.deleteIfExists(invoicePath)
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
        invoicesList.get(0).id == updatedId
        invoicesList.get(0).number == updatedInvoice.number
        invoicesList.get(0).buyer.taxIdentification == updatedInvoice.buyer.taxIdentification
        invoicesList.get(0).buyer.name == updatedInvoice.buyer.name
        invoicesList.get(0).seller.taxIdentification == updatedInvoice.seller.taxIdentification
        invoicesList.get(0).seller.name == updatedInvoice.seller.name

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

    def "toObject() should throw an exception in case of wrong data"() {
        given:
        def invalidData = "{ invalid: json }"

        when:
        jsonService.toObject(invalidData, Invoice.class)

        then:
        thrown(RuntimeException.class)
    }

    def "toJson() should throw an exception in case of wrong object"() {
        given:
        def invalidObject = new Object()

        when:
        jsonService.toJson(invalidObject)

        then:
        thrown(RuntimeException.class)
    }
}
