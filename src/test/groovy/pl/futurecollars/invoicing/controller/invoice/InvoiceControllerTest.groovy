package pl.futurecollars.invoicing.controller.invoice

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.TestHelper
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.model.Vat
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification
import spock.lang.Stepwise

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Stepwise
@AutoConfigureMockMvc
@SpringBootTest
class InvoiceControllerTest extends Specification {

    @Autowired
    private MockMvc mvc

    @Autowired
    private JsonService jsonService

    def "should return 204 (NO_CONTENT) status code when database is empty"() {
        when:
        def expResponse = mvc.perform(get("/invoices/get/all"))
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
                post("/invoices/add/")
                        .content(firstAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andReturn()
                .response
                .contentAsString

        def secondAdded = mvc.perform(
                post("/invoices/add/")
                        .content(secondAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andReturn()
                .response
                .contentAsString

        def thirdAdded = mvc.perform(
                post("/invoices/add/")
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
        def response = mvc.perform(get("/invoices/get/all"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def expInvoices = jsonService.toObject(response, Invoice[].class)

        then:
        expInvoices.size() == 3
        expInvoices[0] == TestHelper.getInvoice()[0]
        expInvoices[1] == TestHelper.getInvoice()[1]
        expInvoices[2] == TestHelper.getInvoice()[2]
    }

    def "should return an invoice if contain searched id=2"() {
        when:
        def response = mvc.perform(get("/invoices/get/2"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def searchedInvoice = jsonService.toObject(response, Invoice.class)

        then:
        searchedInvoice.getId() == 2
        searchedInvoice == TestHelper.getInvoice()[1]
    }

    def "should return nothing if invoice with specific id not exists"() {
        when:
        def response = mvc.perform(get("/invoices/get/5"))
                .andExpect(status().isNoContent())
                .andReturn()
                .response
                .contentAsString

        then:
        response == ""
    }

    def "should return 204 status code (NO CONTENT) if updating invoice does not exits"() {
        given:
        Company updateBuyer = new Company("555-444-22-11", "00-100 Warszawa, ul.Wiejska 18", "DRAGON")
        Company updateSeller = new Company("800-700-40-10", "97-400 Adamow, ul.Jasna 45", "FUTURE")
        List<InvoiceEntry> updateEntry = List.of(new InvoiceEntry("SOME NEW DESCRIPTION", 2, BigDecimal.valueOf(2500), BigDecimal.valueOf(575), Vat.VAT_23))
        Invoice updatedInvoice = new Invoice(5, LocalDate.of(2020, 8, 19), updateBuyer, updateSeller, updateEntry)

        def updatedAsJson = jsonService.toJson(updatedInvoice)

        when:
        def response = mvc.perform(put("/invoices/update/5")
                .content(updatedAsJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn()
                .response
                .contentAsString

        then:
        response == ""
    }

    def "should update (if exists) stored invoice by specific id (1)"() {
        given:
        Company updateBuyer = new Company("555-444-22-11", "00-100 Warszawa, ul.Wiejska 18", "DRAGON")
        Company updateSeller = new Company("800-700-40-10", "97-400 Adamow, ul.Jasna 45", "FUTURE")
        List<InvoiceEntry> updateEntry = List.of(new InvoiceEntry("SOME NEW DESCRIPTION", 2, BigDecimal.valueOf(2500), BigDecimal.valueOf(575), Vat.VAT_23))
        Invoice updatedInvoice = new Invoice(1, LocalDate.now(), updateBuyer, updateSeller, updateEntry)


        def updatedAsJson = jsonService.toJson(updatedInvoice)

        expect:
        def response = mvc.perform(put("/invoices/update/1")
                .content(updatedAsJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def updateResult = mvc.perform(get("/invoices/get/all"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def expInvoices = jsonService.toObject(updateResult, Invoice[].class)

        response == "Invoice with ID: 1 has been successfully updated"

        expInvoices.size() == 3
        expInvoices[0] == updatedInvoice
        expInvoices[1] == TestHelper.getInvoice()[1]
        expInvoices[2] == TestHelper.getInvoice()[2]
    }

    def "should delete invoice with specific id if exists"() {
        when:
        def response = mvc.perform(delete("/invoices/delete/1"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def result = mvc.perform(get("/invoices/get/all"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def expInvoices = jsonService.toObject(result, Invoice[].class)

        then:
        response == "Invoice with ID: 1 has been successfully removed"

        and:
        expInvoices.size() == 2
        expInvoices[0] == TestHelper.getInvoice()[1]
        expInvoices[1] == TestHelper.getInvoice()[2]
    }

    def "should not delete if invoice with specific id does not exist"() {
        when:
        def response = mvc.perform(delete("/invoices/delete/1"))
                .andExpect(status().isNoContent())
                .andReturn()
                .response
                .contentAsString

        def result = mvc.perform(get("/invoices/get/all"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def expInvoices = jsonService.toObject(result, Invoice[].class)

        then:
        response == ""

        and:
        expInvoices.size() == 2
        expInvoices[0] == TestHelper.getInvoice()[1]
        expInvoices[1] == TestHelper.getInvoice()[2]
    }
}
