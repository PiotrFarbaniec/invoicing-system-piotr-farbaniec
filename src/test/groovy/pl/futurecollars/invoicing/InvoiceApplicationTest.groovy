package pl.futurecollars.invoicing

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pl.futurecollars.invoicing.service.CompanyService
import pl.futurecollars.invoicing.service.InvoiceService
import spock.lang.Specification

@SpringBootTest
class InvoiceApplicationTest extends Specification {

    @Autowired
    private InvoiceService invoiceService

    @Autowired
    private CompanyService companyService

    def "creating of invoice and company service"() {
        expect:
        assert invoiceService != null
        assert companyService != null
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
