package pl.futurecollars.invoicing.db.file

import pl.futurecollars.invoicing.utils.FileService
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class IdServiceTest extends Specification {

    def provider = new PathProvider("INVOICE_TEST.txt", "ID_TEST.txt")
    def fileService = new FileService()
    def jsonService = new JsonService()
    def idService = new IdService(fileService, jsonService, provider)
    Path idPath = provider.idPath
    Path invoicePath = provider.invoicePath


    def "getNextIdAndIncrement throw na exception when path to file is null"() {
        when:
        fileService.writeToFile(null, "invoice1")
        idService.getNextIdAndIncrement()

        then:
        thrown(RuntimeException.class)

        cleanup:
        Files.deleteIfExists(invoicePath)
        Files.deleteIfExists(idPath)
    }

    def "getNextIdAndIncrement throw na exception when content is null"() {
        when:
        fileService.writeToFile(invoicePath, null)
        idService.getNextIdAndIncrement()

        then:
        thrown(RuntimeException.class)

        cleanup:
        Files.deleteIfExists(invoicePath)
        Files.deleteIfExists(idPath)
    }

}
