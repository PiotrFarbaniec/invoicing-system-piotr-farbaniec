package pl.futurecollars.invoicing.utils

import spock.lang.Specification

import java.nio.file.Path

class ArgumentValidatorTest extends Specification {
    def "validator should check an argument and not throw exception"() {
    given:
    def filePath = Path.of("SomeFilePath.txt")

        when:
        ArgumentValidator.ensureArgumentNotNull(filePath, 'filePath')

        then:
        notThrown(IllegalArgumentException.class)
    }

    def "should throw an exception when argument is null"() {
        given:
        Integer arg = null

        when:
        ArgumentValidator.ensureArgumentNotNull(arg, "arg")

        then:
        thrown(IllegalArgumentException.class)
    }
}
