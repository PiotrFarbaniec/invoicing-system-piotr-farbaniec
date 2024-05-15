package pl.futurecollars.invoicing.controller.tax

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.TestHelper
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.service.TaxCalculatorResult
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
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
        given:
        def company = Company.builder()
                .address("00-100 Warszawa, Polna 7")
                .healthInsurance(BigDecimal.ZERO)
                .name("No Name S.A.")
                .pensionInsurance(BigDecimal.ZERO)
                .taxIdentification("444-333-22-11")
                .build()

        def companyAsJson = jsonService.toJson(company)

        when:
        def expResponse = mvc.perform(
                post("/tax/company/")
                .content(companyAsJson)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def response = jsonService.toObject(expResponse, TaxCalculatorResult.class)

        then:
        response.income == 0
        response.costs == 0
        response.earnings == 0
        response.pensionInsurance == 0
        response.earningsMinusPensionInsurance == 0
        response.earningsMinusPensionInsuranceRounded == 0
        response.incomeTax == 0
        response.healthInsurancePaid == 0
        response.healthInsuranceToSubtract == 0
        response.incomeTaxMinusHealthInsurance == 0
        response.finalIncomeTax == 0
        response.collectedVat == 0
        response.paidVat == 0
        response.vatToReturn == 0
    }

    def "tax-controller should return tax values for specified tax id number"() {
        given:
        def company = Company.builder()
                .taxIdentification("500-400-30-20")
                .address("30-200 Krakow, ul.Warszawska 7")
                .name("SELLER S.A.")
                .pensionInsurance(BigDecimal.valueOf(514.57))
                .healthInsurance(BigDecimal.valueOf(319.94))
                .build()

        def firstInvoice = TestHelper.getInvoiceForTaxCalculator()[0]
        def secondInvoice = TestHelper.getInvoiceForTaxCalculator()[1]
        def firstAsJson = jsonService.toJson(firstInvoice)
        def secondAsJson = jsonService.toJson(secondInvoice)
        def companyAsJson = jsonService.toJson(company)

        when:
        mvc.perform(
                post("/invoices/add/")
                        .content(firstAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
        mvc.perform(
                post("/invoices/add/")
                        .content(secondAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )

        def expResponse = mvc.perform(
                post("/tax/company/")
                        .content(companyAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def response = jsonService.toObject(expResponse, TaxCalculatorResult.class)

        then:
        response.income == 60490.00                                // 50 000 + 10 490
        response.costs == 6229.48                                  // 2730 + 3138.55 + 3138.55 * 0.23 * 0.5
        response.earnings == 54260.52                              // 60 490 - 6229.48
        response.pensionInsurance == 514.57
        response.earningsMinusPensionInsurance == 53745.95         // 54260.52 - 514.57
        response.earningsMinusPensionInsuranceRounded == 53746.00  // 53746 * 0.19
        response.incomeTax == 10211.74
        response.healthInsurancePaid == 319.94
        response.healthInsuranceToSubtract == 275.50
        response.incomeTaxMinusHealthInsurance == 9936.24           //10211.74 - 275.50
        response.finalIncomeTax == 9936.00
        response.collectedVat == 13912.70                           // 60490 * 0.23
        response.paidVat == 988.83                                  // 5 * 546 * 0.23 + 3138.55 * 0.23 * 0.5
        response.vatToReturn == 12923.87                            // 13912.70 - 988.83
    }
}
