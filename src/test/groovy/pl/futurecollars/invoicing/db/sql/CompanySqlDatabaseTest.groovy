package pl.futurecollars.invoicing.db.sql

import org.flywaydb.core.Flyway
import org.springframework.test.annotation.IfProfileValue
import pl.futurecollars.invoicing.TestHelper

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.model.Company
import spock.lang.Specification

import javax.sql.DataSource

@IfProfileValue(name = "spring.profile.active", values = ["sql"])
class CompanySqlDatabaseTest extends Specification {

    private Database<Company> database

    void setup() {
        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build()
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource)

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("db/migration")
                .load()

        flyway.clean()
        flyway.migrate()

        database = new CompanySqlDatabase(jdbcTemplate)
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

    def "when save() method is called then company should be saved in database"() {
        given:
        def firstCompany = TestHelper.getInvoiceForTaxCalculator()[0].buyer
        def secondCompany = TestHelper.getInvoiceForTaxCalculator()[0].seller
        firstCompany.setId(1)
        secondCompany.setId(2)

        when:
        database.save(firstCompany)
        database.save(secondCompany)

        then:
        database.getAll().size() == 2
        database.getAll().get(0).taxIdentification == firstCompany.taxIdentification
        database.getAll().get(0).name == firstCompany.name
        database.getAll().get(0).address == firstCompany.address
        database.getAll().get(1).taxIdentification == secondCompany.taxIdentification
        database.getAll().get(1).name == secondCompany.name
        database.getAll().get(1).address == secondCompany.address
    }

    def "company should be removed if exist"() {
        given:
        def firstCompany = TestHelper.getInvoiceForTaxCalculator()[0].seller
        def secondCompany = TestHelper.getInvoiceForTaxCalculator()[0].buyer
        database.save(firstCompany)
        database.save(secondCompany)

        when:
        database.delete(1)

        then:
        database.getAll().size() == 1
        database.getById(1) == Optional.empty()
        database.getById(2).isPresent()

    }

    def "no company should be removed if specified id does not exist"() {
        given:
        def firstCompany = TestHelper.getInvoiceForTaxCalculator()[0].seller
        def secondCompany = TestHelper.getInvoiceForTaxCalculator()[0].buyer
        database.save(firstCompany)
        database.save(secondCompany)

        def storedCompaniesNumber = database.getAll().size()

        when:
        database.delete(5)

        then:
        storedCompaniesNumber == database.getAll().size()
    }

    def "update() method called should update existing company"() {
        given:
        def originalCompany = TestHelper.getInvoiceForTaxCalculator()[0].buyer
        database.save(originalCompany)
        def updatedCompany = Company.builder()
                .id(originalCompany.id)
                .taxIdentification("999-999-99-99")
                .address("New address")
                .name("New company name")
                .pensionInsurance(222.22)
                .healthInsurance(333.33)
                .build()

        when:
        database.update(1, updatedCompany)

        then:
        database.getById(1).get().id == 1
        database.getById(1).get().address == "New address"
        database.getById(1).get().name == "New company name"
        database.getById(1).get().taxIdentification == "999-999-99-99"
        database.getById(1).get().pensionInsurance == 222.22
        database.getById(1).get().healthInsurance == 333.33
    }

    def "update() method called should not update if company does not exist"() {
        given:
        def originalCompany = TestHelper.getInvoiceForTaxCalculator()[0].buyer
        database.save(originalCompany)
        def updatedCompany = Company.builder()
                .id(originalCompany.id)
                .taxIdentification("999-999-99-99")
                .address("New address")
                .name("New company name")
                .pensionInsurance(222.22)
                .healthInsurance(333.33)
                .build()

        when:
        database.update(3, updatedCompany)

        then:
        database.getById(3).isEmpty()
        database.getById(1).isPresent()
        database.getById(1).get().address != "New address"
        database.getById(1).get().name != "New company name"
        database.getById(1).get().taxIdentification != "999-999-99-99"
        database.getById(1).get().pensionInsurance != 222.22
        database.getById(1).get().healthInsurance != 333.33
    }
}
