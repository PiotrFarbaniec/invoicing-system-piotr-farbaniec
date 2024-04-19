package pl.futurecollars.invoicing.controller.tax

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.TestHelper
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@SpringBootTest
class TaxCalculatorControllerTest extends Specification {

    @Autowired
    private MockMvc mvc

    @Autowired
    private JsonService jsonService

    def "tax-controller should return 200 (OK) and null values when no invoices in database"() {
        when:
        def expResponse = mvc.perform(get("/tax/333-222-11-11"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        then:
        expResponse == "{\"incomingVat\":0,\"outgoingVat\":0,\"income\":0,\"costs\":0,\"earnings\":0,\"vatToPay\":0}"
    }

    def "tax-controller should return tax values for specified tax id number"() {
        given:
        def invoice = TestHelper.getInvoice()[0]
        def buyerId = "423-456-78-90"
        def sellerId = "538-321-55-32"
        def invoiceAsJson = jsonService.toJson(invoice)

        mvc.perform(
                post("/invoices/add/")
                        .content(invoiceAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )

        when:
        def expResponse1 = mvc.perform(get("/tax/$buyerId"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def expResponse2 = mvc.perform(get("/tax/$sellerId"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        then:
        expResponse1 == "{\"incomingVat\":0,\"outgoingVat\":575,\"income\":0,\"costs\":2500,\"earnings\":-2500,\"vatToPay\":-575}"

        and:
        expResponse2 == "{\"incomingVat\":575,\"outgoingVat\":0,\"income\":2500,\"costs\":0,\"earnings\":2500,\"vatToPay\":575}"
    }
}
