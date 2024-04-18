package pl.futurecollars.invoicing.model

import pl.futurecollars.invoicing.TestHelper
import spock.lang.Specification

class CompanyTest extends Specification {

   Company company1 = TestHelper.getInvoice()[0].getBuyer()
   Company company2 = TestHelper.getInvoice()[0].getSeller()


    Company nullCompany = null

    def "should set new tax id"() {
        setup:
        def oldId = company1.getTaxIdentification()
        def newId = "500-777-50-40"

        when:
        company1.setTaxIdentification(newId)

        then:
        company1.getTaxIdentification() == newId
        company1.getTaxIdentification() != oldId
    }

    def "should set new company address"() {
        setup:
        def oldAddress = company1.getAddress()
        def newAddress = "61-324 Poznań, ul.Wincentego Witosa 18"

        when:
        company1.setAddress(newAddress)

        then:
        company1.getAddress() == newAddress
        company1.getAddress() != oldAddress
    }

    def "should set new company name"() {
        setup:
        def oldName = company1.getName()
        def newName = "Energon"

        when:
        company1.setName(newName)

        then:
        company1.getName() == newName
        company1.getName() != oldName
    }

    def "equals() should return true for the same objects"() {
        when:
        def givenName = "COMPLEX"

        then:
        assert givenName.equals(company1.getName())
    }

    def "hashCode() should return some value"() {
        when:
        company1.hashCode()

        then:
        company1.hashCode() != 0
    }

    def "on empty object hashCode should thrown exception"() {
        when:
        nullCompany.hashCode()

        then:
        thrown(NullPointerException)
    }

    def "toString() should return String value of object"() {
        setup:
        def expResult = "423-456-78-90"

        when:
        def result = company1.getTaxIdentification().toString()

        then:
        assert expResult.contains(result)
    }

    def "for null object toString should return 'null'"() {
        when:
        nullCompany.toString()

        then:
        nullCompany.toString() == "null"
    }

    def "additional cases for equals() nad hashCode() methods"() {
        when:
        def comp1 = company1.toString()
        def comp2 = company2.toString()

        then:
        comp1 != comp2

        when:
        def company = nullCompany

        then:
        !company.equals(company2)
        !company.equals(company1)

        when:
        company1.hashCode()
        company2.hashCode()

        then:
        company1.hashCode() != company2.hashCode()
    }
}


