package pl.futurecollars.invoicing.controller.company

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.Repository
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.TestHelper
import pl.futurecollars.invoicing.db.jpa.CompanyRepository
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@SpringBootTest
class CompanyControllerTest extends Specification {

    @Autowired
    private MockMvc mvc

    @Autowired
    private JsonService jsonService

    @Autowired
    private CompanyRepository companyRepository


    def "should return 204 (NO_CONTENT) status code when no companies in database"() {
        when:
        def expResponse = mvc.perform(get("/companies/get/all"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        then:
        expResponse == "[]"
    }


    def "should add single company to database"() {
        given:
        def firstCompany = TestHelper.getInvoice()[0].seller
        def secondCompany = TestHelper.getInvoice()[0].buyer
        def thirdCompany = TestHelper.getInvoice()[1].seller
        firstCompany.setId(1)
        secondCompany.setId(2)
        thirdCompany.setId(3)

        def firstAsJson = jsonService.toJson(firstCompany)
        def secondAsJson = jsonService.toJson(secondCompany)
        def thirdAsJson = jsonService.toJson(thirdCompany)

        when:
        def firstAdded = mvc.perform(
                post("/companies/add/")
                        .content(firstAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andReturn()
                .response
                .contentAsString

        def secondAdded = mvc.perform(
                post("/companies/add/")
                        .content(secondAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andReturn()
                .response
                .contentAsString

        def thirdAdded = mvc.perform(
                post("/companies/add/")
                        .content(thirdAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andReturn()
                .response
                .contentAsString

        then:
        firstAdded == "Company with ID: 1 has been successfully saved"
        secondAdded == "Company with ID: 2 has been successfully saved"
        thirdAdded == "Company with ID: 3 has been successfully saved"
    }


    def "should return all companies when database is not empty"() {
        given:
        def firstCompany = TestHelper.getInvoice()[0].seller
        def secondCompany = TestHelper.getInvoice()[0].buyer
        def thirdCompany = TestHelper.getInvoice()[1].seller

        when:
        def response = mvc.perform(get("/companies/get/all"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def expCompanies = jsonService.toObject(response, Company[].class)

        then:
        expCompanies.size() == 3
    }


    def "should return company if contain searched id=2"() {
        given:
        def secondCompany = TestHelper.getInvoice()[0].buyer

        when:
        def response = mvc.perform(get("/companies/get/2"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def searchedCompany = jsonService.toObject(response, Company.class)

        then:
        searchedCompany.getId() == 2
        searchedCompany.taxIdentification == secondCompany.taxIdentification
        searchedCompany.name == secondCompany.name
        searchedCompany.address == secondCompany.address
    }


    def "should return nothing if company with specific id not exists"() {
        when:
        def response = mvc.perform(get("/companies/get/5"))
                .andExpect(status().isNoContent())
                .andReturn()
                .response
                .contentAsString

        then:
        response == ""
    }


    def "should return 204 status code (NO CONTENT) if company with given id does not exits"() {
        given:

        def updateCompany = Company.builder()
                .taxIdentification("800-700-40-10")
                .address("97-400 Adamow, ul.Jasna 45")
                .name("FUTURE")
                .build()

        def updatedAsJson = jsonService.toJson(updateCompany)

        when:
        def response = mvc.perform(put("/companies/update/5")
                .content(updatedAsJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn()
                .response
                .contentAsString

        then:
        response == ""
    }


    def "company with specific id should be updated if present"() {
        given:
        def originalCompany = TestHelper.getInvoice()[0].seller
        def updateCompany = Company.builder()
                .id(1)
                .taxIdentification("800-700-40-10")
                .address("97-400 Adamow, ul.Jasna 45")
                .name("FUTURE")
                .build()

        def updatedAsJson = jsonService.toJson(updateCompany)

        expect:
        def response = mvc.perform(put("/companies/update/1")
                .content(updatedAsJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def updateResult = mvc.perform(get("/companies/get/all"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def expCompanies = jsonService.toObject(updateResult, Company[].class)

        response == "Company with ID: 1 has been successfully updated"

        expCompanies.size() == 3
        expCompanies[0].id == updateCompany.id
        expCompanies[0].name == updateCompany.name
        expCompanies[0].taxIdentification == updateCompany.taxIdentification
        expCompanies[0].address == updateCompany.address

        expCompanies[1].taxIdentification != updateCompany.taxIdentification
        expCompanies[1].name != updateCompany.name
        expCompanies[2].taxIdentification != updateCompany.taxIdentification
        expCompanies[2].name != updateCompany.name
    }


    def "should delete company with specific id if exists"() {
        when:
        def response = mvc.perform(delete("/companies/delete/1"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def result = mvc.perform(get("/companies/get/all"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def expCompanies = jsonService.toObject(result, Company[].class)

        then:
        response == "Company with ID: 1 has been successfully removed"
        expCompanies.size() == 2
    }


    def "should not delete if company with specific id does not exist"() {
        when:
        def response = mvc.perform(delete("/companies/delete/1"))
                .andExpect(status().isNoContent())
                .andReturn()
                .response
                .contentAsString

        def result = mvc.perform(get("/companies/get/all"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def expCompanies = jsonService.toObject(result, Company[].class)

        then:
        response == ""
        expCompanies.size() == 2

//        cleanupSpec:
//            companyRepository.deleteAll()
    }
}
