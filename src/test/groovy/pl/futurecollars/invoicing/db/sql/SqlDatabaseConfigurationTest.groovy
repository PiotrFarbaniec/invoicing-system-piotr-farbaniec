package pl.futurecollars.invoicing.db.sql

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

@SpringBootTest(classes = SqlDatabaseConfiguration.class)
@ActiveProfiles("sql")
class SqlDatabaseConfigurationTest extends Specification {

    @Autowired
    Database<Invoice> invoiceSqlDatabase

    @Autowired
    Database<Company> companySqlDatabase

    @MockBean
    JdbcTemplate jdbcTemplate

    def "should create SqlDatabase bean"() {
        expect:
        invoiceSqlDatabase != null
    }

}
