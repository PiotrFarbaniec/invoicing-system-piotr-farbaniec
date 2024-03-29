package pl.futurecollars.invoicing.utils

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class FileServiceTest extends Specification {

    def testPath = Path.of("TEST FILE.txt")
    FileService fileService = new FileService()

    def "appendLineToFile() should append each single line to file"() {
        given:
        def line1 = "First line"
        def line2 = "Second line"
        def line3 = "Third line"
        Files.createFile(testPath)

        when:
        fileService.appendLineToFile(testPath, line1)
        fileService.appendLineToFile(testPath, line2)
        fileService.appendLineToFile(testPath, line3)
        def result = fileService.readAllLines(testPath)

        then:
        result.size() == 3
        result.get(0) == line1
        result.get(1) == line2
        result.get(2) == line3

        cleanup:
        Files.deleteIfExists(testPath)
    }

    def "appendLineToFile() should throw an exception when path or content is null"() {
        given:
        def line1 = "First line"
        Files.createFile(testPath)

        when:
        fileService.appendLineToFile(testPath, null)

        then:
        thrown(RuntimeException.class)

        when:
        fileService.appendLineToFile(null, line1)

        then:
        thrown(RuntimeException.class)

        cleanup:
        Files.deleteIfExists(testPath)
    }

    def "writeToFile() should write given content to file"() {
        given:
        def line = "Some content which should be added to a file"
        Files.createFile(testPath)

        when:
        fileService.writeToFile(testPath, line)

        then:
        fileService.readAllLines(testPath).get(0) == "Some content which should be added to a file"

        cleanup:
        Files.deleteIfExists(testPath)
    }

    def "writeToFile() should throw an exception when path or content is null"() {
        given:
        def line = "Some content"
        Files.createFile(testPath)

        when:
        fileService.writeToFile(testPath, null)

        then:
        thrown(RuntimeException.class)

        when:
        fileService.writeToFile(null, line)

        then:
        thrown(RuntimeException.class)

        cleanup:
        Files.deleteIfExists(testPath)
    }

    def "writeLinesToFile() should write all given lines to file"() {
        given:
        def lines = List.of("line1", "line2", "line3", "line4", "line5")
        Files.createFile(testPath)

        when:
        fileService.writeLinesToFile(testPath, lines)

        then:
        fileService.readAllLines(testPath).size() == 5
        fileService.readAllLines(testPath).get(0) == "line1"
        fileService.readAllLines(testPath).get(4) == "line5"

        cleanup:
        Files.deleteIfExists(testPath)
    }

    def "writeLinesToFile() should throw an exception for null arguments"() {
        given:
        def lines = List.of("line1", "line2")
        Files.createFile(testPath)

        when:
        fileService.writeLinesToFile(null, lines)

        then:
        thrown(RuntimeException.class)

        when:
        fileService.writeLinesToFile(testPath, null)

        then:
        thrown(RuntimeException.class)

        cleanup:
        Files.deleteIfExists(testPath)
    }

    def "appendLinesToFile() should append any multi-line content to file"() {
        given:
        def lines1 = List.of("line1", "line2", "line3")
        def lines2 = List.of("line4", "line5", "line6")
        Files.createFile(testPath)

        when:
        fileService.appendLinesToFile(testPath, lines1)
        fileService.appendLinesToFile(testPath, lines2)

        then:
        fileService.readAllLines(testPath).size() == 6
        fileService.readAllLines(testPath).get(0) == "line1"
        fileService.readAllLines(testPath).get(5) == "line6"

        cleanup:
        Files.deleteIfExists(testPath)
    }

    def "appendLinesToFile() should throw an exception for null arguments"() {
        given:
        def lines = List.of("line1", "line2")
        Files.createFile(testPath)

        when:
        fileService.appendLinesToFile(null, lines)

        then:
        thrown(RuntimeException.class)

        when:
        fileService.appendLinesToFile(testPath, null)

        then:
        thrown(RuntimeException.class)

        cleanup:
        Files.deleteIfExists(testPath)
    }

    def "readAllLines() should throw an exception for null path"() {
        when:
        fileService.readAllLines(null)

        then:
        thrown(RuntimeException.class)
    }
}
