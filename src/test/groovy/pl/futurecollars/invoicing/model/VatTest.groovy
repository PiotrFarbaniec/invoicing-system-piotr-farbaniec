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
        Vat.VAT_23   || 0.23
        Vat.VAT_19   || 0.19
        Vat.VAT_9    || 0.09
        Vat.VAT_8    || 0.08
        Vat.VAT_7_75 || 0.0775
        Vat.VAT_5    || 0.05
        Vat.VAT_0    || 0.0
    }

    def "should have correct number of values"() {
        expect:
        Vat.values().length == 7
    }
}
