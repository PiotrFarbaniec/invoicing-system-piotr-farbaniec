package pl.futurecollars.invoicing

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pl.futurecollars.invoicing.service.InvoiceService
import spock.lang.Specification

@SpringBootTest
class InvoiceApplicationTest extends Specification {

    @Autowired
    private InvoiceService service

    def "ceating invoice service"() {
        expect:
        service
    }

    def "simple test to successfully launch the Spring Boot application"() {
        given:
        def api = new InvoiceApplication()

        when:
        api.main()

        then:
        noExceptionThrown()
    }
}
