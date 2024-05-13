package pl.futurecollars.invoicing.db.sql

import org.flywaydb.core.Flyway
import pl.futurecollars.invoicing.TestHelper

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.model.Car
import spock.lang.Specification

import javax.sql.DataSource
import java.time.LocalDate


class SqlDatabaseTest extends Specification {

    Database database

    void setup() {
        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build()
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource)

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("db/migration")
                .load()

        flyway.clean()
        flyway.migrate()

        database = new SqlDatabase(jdbcTemplate)
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

    def "when delete() an invoice should be removed if exist"() {
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

    /*def "update() method called should not update if invoice not exist"() {
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
        database.update(2, updatedInvoice)

        then:
        originalInvoice.date == updatedInvoice.date
        originalInvoice.number == updatedInvoice.number
        originalInvoice.entries[0].description == updatedInvoice.entries[0].description
        updatedInvoice.entries[0].description != "New description of first invoice entry"
        originalInvoice.entries[1].description == updatedInvoice.entries[1].description
        updatedInvoice.entries[1].description != "New description of second invoice entry"
        originalInvoice.entries.size() != updatedInvoice.entries.size()
    }*/

    /*def "should not save a car if registration number field is empty"() {
        given:
        def invoice = TestHelper.getInvoiceForTaxCalculator()[0]
        def emptyCar = Car.builder()
        .registrationNumber("")
        .isUsedPrivately(true)

        invoice.entries[0].setCarRelatedExpenses(Optional.ofNullable(emptyCar))

        when:
        def savedInvoice = database.save(invoice)

        then:
        savedInvoice.entries[0].carRelatedExpenses.registrationNumber.empty
        savedInvoice.entries[0].carRelatedExpenses.isUsedPrivately() == false
    }*/
}