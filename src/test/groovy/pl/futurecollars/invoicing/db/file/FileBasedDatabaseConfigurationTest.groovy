package pl.futurecollars.invoicing.db.file

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import pl.futurecollars.invoicing.utils.FileManager
import pl.futurecollars.invoicing.utils.FileService
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification


@SpringBootTest(classes = FileBasedDatabaseConfiguration.class)
@ActiveProfiles("file")
class FileBasedDatabaseConfigurationTest extends Specification {

    @Autowired
    IdService invoiceIdService

    @MockBean
    FileService fileService

    @MockBean
    FileManager fileManager

    @MockBean
    JsonService jsonService


    def "should create InvoiceIdService bean"() {
        expect:
        invoiceIdService != null
    }
}
