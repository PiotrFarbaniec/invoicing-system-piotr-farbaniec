package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxCalculatorResult {

  private BigDecimal income;
  private BigDecimal costs;
  private BigDecimal earnings;
  private BigDecimal pensionInsurance;
  private BigDecimal earningsMinusPensionInsurance;
  private BigDecimal earningsMinusPensionInsuranceRounded;
  private BigDecimal incomeTax;
  private BigDecimal healthInsurancePaid;
  private BigDecimal healthInsuranceToSubtract;
  private BigDecimal incomeTaxMinusHealthInsurance;
  private BigDecimal finalIncomeTax;
  private BigDecimal collectedVat;
  private BigDecimal paidVat;
  private BigDecimal vatToReturn;
}
