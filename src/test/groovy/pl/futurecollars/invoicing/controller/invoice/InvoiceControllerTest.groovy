package pl.futurecollars.invoicing.controller.invoice

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.TestHelper
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.model.Car
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.model.Vat
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification
import spock.lang.Stepwise

import java.time.LocalDate

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WithMockUser
@AutoConfigureMockMvc
@SpringBootTest
@Stepwise
class InvoiceControllerTest extends Specification {

    @Autowired
    private MockMvc mvc

    @Autowired
    private JsonService jsonService

    @Autowired
    private Database<Invoice> database

    def "database is dropped to ensure clean state"(){
        expect:
        database != null

        when:
        database.reset()

        then:
        database.getAll().size == 0
    }

    def "should return 204 (NO_CONTENT) status code when database is empty"() {
        when:
        def expResponse = mvc.perform(get("/invoices/get/all").with(csrf()))
                .andExpect(status().isNoContent())
                .andReturn()
                .response
                .contentAsString

        then:
        expResponse == ""
    }

    def "should add single invoice to database"() {
        given:
        def firstInvoice = TestHelper.getInvoice()[0]
        def secondInvoice = TestHelper.getInvoice()[1]
        def thirdInvoice = TestHelper.getInvoice()[2]

        def firstAsJson = jsonService.toJson(firstInvoice)
        def secondAsJson = jsonService.toJson(secondInvoice)
        def thirdAsJson = jsonService.toJson(thirdInvoice)

        when:
        def firstAdded = mvc.perform(
                post("/invoices/add/").with(csrf())
                        .content(firstAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andReturn()
                .response
                .contentAsString

        def secondAdded = mvc.perform(
                post("/invoices/add/").with(csrf())
                        .content(secondAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andReturn()
                .response
                .contentAsString

        def thirdAdded = mvc.perform(
                post("/invoices/add/").with(csrf())
                        .content(thirdAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andReturn()
                .response
                .contentAsString

        then:
        firstAdded == "Invoice with ID: 1 has been successfully saved"
        secondAdded == "Invoice with ID: 2 has been successfully saved"
        thirdAdded == "Invoice with ID: 3 has been successfully saved"
    }

    def "should return all invoices when database is not empty"() {
        when:
        def response = mvc.perform(get("/invoices/get/all").with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def expInvoices = jsonService.toObject(response, Invoice[].class)

        then:
        expInvoices.size() == 3
        expInvoices[0].number == TestHelper.getInvoice()[0].number
        expInvoices[1].number == TestHelper.getInvoice()[1].number
        expInvoices[2].number == TestHelper.getInvoice()[2].number
    }

    def "should return an invoice if contain searched id=2"() {
        when:
        def response = mvc.perform(get("/invoices/get/2").with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def searchedInvoice = jsonService.toObject(response, Invoice.class)

        then:
        searchedInvoice.getId() == 2
        searchedInvoice.number == TestHelper.getInvoice()[1].number
        searchedInvoice.buyer.taxIdentification == TestHelper.getInvoice()[1].buyer.taxIdentification
        searchedInvoice.buyer.name == TestHelper.getInvoice()[1].buyer.name
        searchedInvoice.seller.taxIdentification == TestHelper.getInvoice()[1].seller.taxIdentification
        searchedInvoice.seller.name == TestHelper.getInvoice()[1].seller.name
        searchedInvoice.entries.size() == TestHelper.getInvoice()[1].entries.size()
        searchedInvoice.entries[0].description == TestHelper.getInvoice()[1].entries[0].description
        searchedInvoice.entries[1].description == TestHelper.getInvoice()[1].entries[1].description
    }

    def "should return nothing if invoice with specific id not exists"() {
        when:
        def response = mvc.perform(get("/invoices/get/5").with(csrf()))
                .andExpect(status().isNoContent())
                .andReturn()
                .response
                .contentAsString

        then:
        response == ""
    }

    def "should return 204 status code (NO CONTENT) if updating invoice does not exits"() {
        given:
        def updateBuyer = Company.builder()
                .taxIdentification("555-444-22-11")
                .address("00-100 Warszawa, ul.Wiejska 18")
                .name("DRAGON")
                .build()

        def updateSeller = Company.builder()
                .taxIdentification("800-700-40-10")
                .address("97-400 Adamow, ul.Jasna 45")
                .name("FUTURE")
                .build()

        def updateEntry = List.of(InvoiceEntry.builder()
                .description("SOME NEW DESCRIPTION")
                .quantity(2)
                .netPrice(BigDecimal.valueOf(2500.00))
                .vatValue(BigDecimal.valueOf(575.00))
                .vatRate(Vat.VAT_23)
                .build())

        def updatedInvoice = Invoice.builder()
                .id(5)
                .date(LocalDate.of(2020, 8, 19))
                .buyer(updateBuyer)
                .seller(updateSeller)
                .entries(updateEntry)
                .build()

        def updatedAsJson = jsonService.toJson(updatedInvoice)

        when:
        def response = mvc.perform(put("/invoices/update/5").with(csrf())
                .content(updatedAsJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn()
                .response
                .contentAsString

        then:
        response == ""
    }

    def "invoice with specific id should be updated if present"() {
        given:
        def updateBuyer = Company.builder()
                .id(1)
                .taxIdentification("555-444-22-11")
                .address("00-100 Warszawa, ul.Wiejska 18")
                .name("DRAGON")
                .build()

        def updateSeller = Company.builder()
                .id(2)
                .taxIdentification("800-700-40-10")
                .address("97-400 Adamow, ul.Jasna 45")
                .name("FUTURE")
                .build()

        def updateEntry = List.of(
                InvoiceEntry.builder()
                        .id(1)
                        .description("SOME NEW DESCRIPTION 1")
                        .quantity(2)
                        .netPrice(BigDecimal.valueOf(2000.25))
                        .vatValue(BigDecimal.valueOf(500.55))
                        .vatRate(Vat.VAT_23)
                        .build(),
                InvoiceEntry.builder()
                        .id(2)
                        .description("SOME NEW DESCRIPTION 2")
                        .quantity(2)
                        .netPrice(BigDecimal.valueOf(2050.00))
                        .vatValue(BigDecimal.valueOf(155.00))
                        .vatRate(Vat.VAT_23)
                        .build())

        def updatedInvoice = Invoice.builder()
                .id(1)
                .number("INV_000012")
                .date(LocalDate.of(2022, 8, 12))
                .buyer(updateBuyer)
                .seller(updateSeller)
                .entries(updateEntry)
                .build()

        def updatedAsJson = jsonService.toJson(updatedInvoice)

        expect:
        def response = mvc.perform(put("/invoices/update/1").with(csrf())
                .content(updatedAsJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def updateResult = mvc.perform(get("/invoices/get/all").with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def expInvoices = jsonService.toObject(updateResult, Invoice[].class)

        response == "Invoice with ID: 1 has been successfully updated"

        expInvoices.size() == 3
        expInvoices[0].number == updatedInvoice.number
        expInvoices[0].date == updatedInvoice.date
        expInvoices[0].seller.name == updatedInvoice.seller.name
        expInvoices[0].seller.taxIdentification == updatedInvoice.seller.taxIdentification
        expInvoices[0].buyer.name == updatedInvoice.buyer.name
        expInvoices[0].buyer.taxIdentification == updatedInvoice.buyer.taxIdentification
        expInvoices[0].entries[0].description == updateEntry[0].description
        expInvoices[0].entries[1].description == updateEntry[1].description
    }

    def "should not update any invoice if specified id does not exist"() {
        given:
        def updateBuyer = Company.builder()
                .id(1)
                .taxIdentification("555-444-22-11")
                .address("00-100 Warszawa, ul.Wiejska 18")
                .name("DRAGON")
                .build()

        def updateSeller = Company.builder()
                .id(2)
                .taxIdentification("800-700-40-10")
                .address("97-400 Adamow, ul.Jasna 45")
                .name("FUTURE")
                .build()

        def updateEntry = List.of(
                InvoiceEntry.builder()
                        .id(1)
                        .description("SOME NEW DESCRIPTION 1")
                        .quantity(2)
                        .netPrice(BigDecimal.valueOf(2000.25))
                        .vatValue(BigDecimal.valueOf(500.55))
                        .vatRate(Vat.VAT_23)
                        .carRelatedExpenses(Car.builder()
                                .id(1)
                                .registrationNumber(" ")
                                .isUsedPrivately(true)
                                .build())
                        .build(),
                InvoiceEntry.builder()
                        .id(2)
                        .description("SOME NEW DESCRIPTION 2")
                        .quantity(2)
                        .netPrice(BigDecimal.valueOf(2050.00))
                        .vatValue(BigDecimal.valueOf(155.00))
                        .vatRate(Vat.VAT_23)
                        .carRelatedExpenses(Car.builder()
                                .id(2)
                                .registrationNumber("WW 22233")
                                .isUsedPrivately(true)
                                .build())
                        .build())

        def updatedInvoice = Invoice.builder()
                .id(1)
                .number("INV_000012")
                .date(LocalDate.of(2022, 8, 12))
                .buyer(updateBuyer)
                .seller(updateSeller)
                .entries(updateEntry)
                .build()

        def updatedAsJson = jsonService.toJson(updatedInvoice)

        expect:
        def response = mvc.perform(put("/invoices/update/6").with(csrf())
                .content(updatedAsJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent())
                .andReturn()
                .response
                .contentAsString

        def updateResult = mvc.perform(get("/invoices/get/all").with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def expInvoices = jsonService.toObject(updateResult, Invoice[].class)

        response == ""
        expInvoices.size() == 3
        expInvoices[0].number == updatedInvoice.number
        expInvoices[1].number == TestHelper.getInvoice()[1].number
        expInvoices[2].number == TestHelper.getInvoice()[2].number
        expInvoices[0].id == 1
        expInvoices[1].id == 2
        expInvoices[2].id == 3
    }

    def "should delete invoice with specific id if exists"() {
        when:
        def response = mvc.perform(delete("/invoices/delete/1").with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def result = mvc.perform(get("/invoices/get/all").with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def expInvoices = jsonService.toObject(result, Invoice[].class)

        then:
        response == "Invoice with ID: 1 has been successfully removed"
        expInvoices.size() == 2
        expInvoices[0].id != TestHelper.getInvoice()[0].id
        expInvoices[0].number != TestHelper.getInvoice()[0].number
        expInvoices[1].id != TestHelper.getInvoice()[0].id
        expInvoices[1].number != TestHelper.getInvoice()[0].number

    }


    def "should not delete if invoice with specific id does not exist"() {
        when:
        def response = mvc.perform(delete("/invoices/delete/1").with(csrf()))
                .andExpect(status().isNoContent())
                .andReturn()
                .response
                .contentAsString

        def result = mvc.perform(get("/invoices/get/all").with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def expInvoices = jsonService.toObject(result, Invoice[].class)

        then:
        response == ""
        expInvoices.size() == 2
        expInvoices[0].id != TestHelper.getInvoice()[0].id
        expInvoices[0].number != TestHelper.getInvoice()[0].number
        expInvoices[1].id != TestHelper.getInvoice()[0].id
        expInvoices[1].number != TestHelper.getInvoice()[0].number
    }
}
