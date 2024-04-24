package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TaxCalculatorResult {

  private final BigDecimal income;
  private final BigDecimal costs;
  private final BigDecimal earnings;
  private final BigDecimal pensionInsurance;
  private final BigDecimal earningsMinusPensionInsurance;
  private final BigDecimal earningsMinusPensionInsuranceRounded;
  private final BigDecimal incomeTax;
  private final BigDecimal healthInsurancePaid;
  private final BigDecimal healthInsuranceToSubtract;
  private final BigDecimal incomeTaxMinusHealthInsurance;
  private final BigDecimal finalIncomeTax;
  private final BigDecimal collectedVat;
  private final BigDecimal paidVat;
  private final BigDecimal vatToReturn;
}
