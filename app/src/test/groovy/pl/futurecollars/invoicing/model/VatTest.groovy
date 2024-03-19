package pl.futurecollars.invoicing.model

import spock.lang.Specification
import spock.lang.Unroll

class VatTest extends Specification {

    @Unroll
    def "should return correct rate for vat case"() {
        expect:
        vat.getRate() == expectedRate

        where:
        vat          || expectedRate
        Vat.VAT_23   || 23
        Vat.VAT_8    || 8
        Vat.VAT_5    || 5
        Vat.VAT_0    || 0
    }

    def "should have correct number of values"() {
        expect:
        Vat.values().length == 4
    }
}
