package pl.futurecollars.invoicing.db.jpa

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.IfProfileValue
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.sql.SqlDatabaseTest
import spock.lang.Specification

@DataJpaTest
@IfProfileValue(name = "spring.profile.active", values = ["jpa"])
class JpaDatabaseTest extends SqlDatabaseTest {

    private Database database

    @Autowired
    private InvoiceRepository repository

    @Override
    void setup() {
        database = new JpaDatabase(repository)
    }
}
