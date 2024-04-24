package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TaxCalculatorResult {

  private final BigDecimal income;  // policzone
  private final BigDecimal costs;   // policzone
  private final BigDecimal earnings;  // 'incomeMinusCosts' policzone (wcześniej jako earnings)
  private final BigDecimal pensionInsurance;
  private final BigDecimal earningsMinusPensionInsurance; //  incomeMinusCostsMinusPensionInsurance
  private final BigDecimal earningsMinusPensionInsuranceRounded;    // incomeMinusCostsMinusPensionInsuranceRounded
  private final BigDecimal incomeTax;
  private final BigDecimal healthInsurancePaid;
  private final BigDecimal healthInsuranceToSubtract;
  private final BigDecimal incomeTaxMinusHealthInsurance;
  private final BigDecimal finalIncomeTax;
  private final BigDecimal collectedVat;  // policzone
  private final BigDecimal paidVat;   // policzone
  private final BigDecimal vatToReturn;   // policzone (wcześniej jako vatToPay)
}
