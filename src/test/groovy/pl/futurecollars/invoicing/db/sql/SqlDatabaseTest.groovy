package pl.futurecollars.invoicing.db.sql

import org.flywaydb.core.Flyway
import org.springframework.test.annotation.IfProfileValue
import pl.futurecollars.invoicing.TestHelper

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import pl.futurecollars.invoicing.db.Database
import spock.lang.Specification

import javax.sql.DataSource
import java.time.LocalDate

@IfProfileValue(name = "spring.profile.active", values = ["sql"])
class SqlDatabaseTest extends Specification {

    private Database database

    void setup() {
        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build()
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource)

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("db/migration")
                .load()

        flyway.clean()
        flyway.migrate()

        database = new InvoiceSqlDatabase(jdbcTemplate)
        database.initlizeVatMap()
    }

    def "getAll() called on empty database should return no values"() {
        when:
        def result = database.getAll()

        then:
        result == []
        result.size() == 0
        database.getById(1) == Optional.empty()
    }

    def "when save() method is called then invoice should be saved in database"() {
        given:
        def firstInvoice = TestHelper.getInvoiceForTaxCalculator()[0]
        firstInvoice.setId(1)
        def secondInvoice = TestHelper.getInvoiceForTaxCalculator()[1]
        secondInvoice.setId(2)

        when:
        database.save(firstInvoice)
        database.save(secondInvoice)
        def firstSavedInvoice = database.getById(1).get()
        def secondSavedInvoice = database.getById(2).get()

        then:
        database.getAll().size() == 2
        firstSavedInvoice.id == 1
        firstSavedInvoice.getNumber() == firstInvoice.getNumber()
        firstSavedInvoice.getDate() == firstInvoice.getDate()
        firstSavedInvoice.getEntries().size() == firstInvoice.getEntries().size()
        firstSavedInvoice.entries[0].description == firstInvoice.entries[0].description
        firstSavedInvoice.entries[1].description == firstInvoice.entries[1].description
    }

    def "invoice should be removed if exist"() {
        given:
        def firstInvoice = TestHelper.getInvoiceForTaxCalculator()[0]
        def secondInvoice = TestHelper.getInvoiceForTaxCalculator()[1]
        database.save(firstInvoice)
        database.save(secondInvoice)

        when:
        database.delete(1)

        then:
        database.getById(1) == Optional.empty()
        database.getById(2).get().id == secondInvoice.id
        database.getById(2).get().entries.size() == secondInvoice.entries.size()
        database.getById(2).get().entries[0].description == secondInvoice.entries[0].description
        database.getById(2).get().entries[1].description == secondInvoice.entries[1].description
    }

    def "no invoice should be removed if specified id does not exist"() {
        given:
        def firstInvoice = TestHelper.getInvoiceForTaxCalculator()[0]
        def secondInvoice = TestHelper.getInvoiceForTaxCalculator()[1]
        database.save(firstInvoice)
        database.save(secondInvoice)
        def storedInvoicesNumber = database.getAll().size()

        when:
        database.delete(5)

        then:
        storedInvoicesNumber == database.getAll().size()
    }

    def "update() method called should update existing invoice"() {
        given:
        def originalInvoice = TestHelper.getInvoiceForTaxCalculator()[0]
        database.save(originalInvoice)
        def updatedDate = LocalDate.of(2023, 10, 23)
        def updatedNumber = "2023_ZW88888"
        def updateDescription1 = "New description of first invoice entry"
        def updateDescription2 = "New description of second invoice entry"
        def updatedInvoice = TestHelper.getInvoiceForTaxCalculator()[0]

        updatedInvoice.setDate(updatedDate)
        updatedInvoice.setNumber(updatedNumber)
        updatedInvoice.getEntries()[0].setDescription(updateDescription1)
        updatedInvoice.getEntries()[1].setDescription(updateDescription2)

        when:
        database.update(1, updatedInvoice)

        then:
        originalInvoice.date != updatedInvoice.date
        originalInvoice.number != updatedInvoice.number
        originalInvoice.entries[0].description != updatedInvoice.entries[0].description
        updatedInvoice.entries[0].description == "New description of first invoice entry"
        originalInvoice.entries[1].description != updatedInvoice.entries[1].description
        updatedInvoice.entries[1].description == "New description of second invoice entry"
        originalInvoice.entries.size() == updatedInvoice.entries.size()
    }

    def "update() method called should not update if invoice does not exist"() {
        given:
        def originalInvoice = TestHelper.getInvoiceForTaxCalculator()[0]
        database.save(originalInvoice)
        def updatedInvoice = TestHelper.getInvoiceForTaxCalculator()[1]
        updatedInvoice.setId(4)
        def originalInvoicesNumber = database.getAll().size()

        when:
        database.update(4, updatedInvoice)

        then:
        originalInvoicesNumber == database.getAll().size()
        originalInvoice.number == database.getById(1).get().number
        originalInvoice.buyer.taxIdentification == database.getById(1).get().buyer.taxIdentification
        originalInvoice.seller.taxIdentification == database.getById(1).get().seller.taxIdentification
        originalInvoice.entries.size() == database.getById(1).get().entries.size()
        originalInvoice.entries[0].description == database.getById(1).get().entries[0].description
        originalInvoice.entries[1].description == database.getById(1).get().entries[1].description

        database.getById(4) == Optional.empty()
    }
}