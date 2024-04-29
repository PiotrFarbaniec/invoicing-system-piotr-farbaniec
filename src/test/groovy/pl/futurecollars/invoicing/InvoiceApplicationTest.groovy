package pl.futurecollars.invoicing

import spock.lang.Specification

class InvoiceApplicationTest extends Specification {

    def "simple test to successfully launch the Spring Boot application"() {
        given:
        def api = new InvoiceApplication()

        when:
        api.main()

        then:
        noExceptionThrown()
    }
}
