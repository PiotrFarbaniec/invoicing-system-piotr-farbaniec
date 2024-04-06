package pl.futurecollars.invoicing.db.file

import pl.futurecollars.invoicing.utils.FileService
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class IdServiceTest extends Specification {

    FileService fileService = Mock(FileService)
    Path idPath = Path.of("ID_TEST.txt")
    IdService idService = new IdService(fileService, idPath)

    def "getNextIdAndIncrement throw an exception when path to file is null"() {
        when:
        fileService.writeToFile(null, "invoice1")
        idService.getNextIdAndIncrement()

        then:
        thrown(RuntimeException.class)

        cleanup:
        Files.deleteIfExists(idPath)
    }

    def "getNextIdAndIncrement throw na exception when content is null"() {
        when:
        fileService.writeToFile(idPath, null)
        idService.getNextIdAndIncrement()

        then:
        thrown(RuntimeException.class)

        cleanup:
        Files.deleteIfExists(idPath)
    }

    def "should throw an exception when path to file is null"() {
        setup:
        IdService idService = new IdService(fileService, null)

        when:
        idService.getNextIdAndIncrement()
        fileService.readAllLines(idPath)
        /*fileService.writeToFile(null, "invoice1")*/

        then:
        thrown(RuntimeException.class)

        cleanup:
        Files.deleteIfExists(idPath)
    }

    def "should return next ID when file read and write succeed"() {
        given:
        fileService.readAllLines(idPath) >> ["42"]
        fileService.writeToFile(idPath, "43")

        when:
        def result = idService.getNextIdAndIncrement()

        then:
        result == 42

        cleanup:
        Files.deleteIfExists(idPath)
    }
}
