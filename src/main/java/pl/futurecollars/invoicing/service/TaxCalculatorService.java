package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Car;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;

@Service
public class TaxCalculatorService {

  private final Database database;

  public TaxCalculatorService(Database database) {
    this.database = database;
  }

  public BigDecimal income(String taxIdNumber) {
    return database.visit(sellerPredicate(taxIdNumber), InvoiceEntry::getNetPrice);
  }

  public BigDecimal costs(String taxIdNumber) {
    return database.visit(buyerPredicate(taxIdNumber), this::getIncomeValueTakingIntoConsiderationPersonalCarUse);
  }

  private BigDecimal getIncomeValueTakingIntoConsiderationPersonalCarUse(InvoiceEntry invoiceEntry) {
    return invoiceEntry.getNetPrice()
        .add(invoiceEntry.getVatValue())
        .subtract(getVatValueTakingIntoConsiderationPersonalCarUse(invoiceEntry));
  }

  public BigDecimal getEarnings(String taxIdNumber) {
    return income(taxIdNumber).subtract(costs(taxIdNumber));
  }

  public BigDecimal collectedVat(String taxIdNumber) {
    return database.visit(sellerPredicate(taxIdNumber), InvoiceEntry::getVatValue);
  }

  public BigDecimal paidVat(String taxIdNumber) {
    return database.visit(buyerPredicate(taxIdNumber), this::getVatValueTakingIntoConsiderationPersonalCarUse);
  }

  private BigDecimal getVatValueTakingIntoConsiderationPersonalCarUse(InvoiceEntry invoiceEntry) {
    return Optional.ofNullable(invoiceEntry.getCarRelatedExpenses())
        .map(Car::isUsedPrivately)
        .map(personalCarUsage -> personalCarUsage ? BigDecimal.valueOf(0.50) : BigDecimal.ONE)
        .map(proportion -> invoiceEntry.getVatValue().multiply(proportion))
        .map(value -> value.setScale(2, RoundingMode.FLOOR))
        .orElse(invoiceEntry.getVatValue());
  }

  public BigDecimal getVatToReturn(String taxIdNumber) {
    return collectedVat(taxIdNumber).subtract(paidVat(taxIdNumber));
  }

  private Predicate<Invoice> sellerPredicate(String taxIdNumber) {
    return invoice -> taxIdNumber.equals(invoice.getSeller().getTaxIdentification());
  }

  private Predicate<Invoice> buyerPredicate(String taxIdNumber) {
    return invoice -> taxIdNumber.equals(invoice.getBuyer().getTaxIdentification());
  }

  public BigDecimal getTaxBase(Company company) {
    return (getEarnings(company.getTaxIdentification())
        .subtract(company.getPensionInsurance()))
        .setScale(2, RoundingMode.HALF_UP);
  }

  public BigDecimal getAboveRoundedTaxBase(Company company) {
    /* BigDecimal originalValue = getTaxBase(company); */
    return getTaxBase(company).setScale(0, RoundingMode.HALF_UP);
  }

  public BigDecimal getIncomeTax(Company company) {
    return (getAboveRoundedTaxBase(company)
        .multiply(BigDecimal.valueOf(Vat.VAT_19.getRate())))
        .setScale(2, RoundingMode.HALF_DOWN);
  }

  public BigDecimal getReducedHealthInsurance(Company company) {
    BigDecimal refInsurance = company.getHealthInsurance();
    return refInsurance.multiply((BigDecimal.valueOf(Vat.VAT_7_75.getRate()))
        .divide(BigDecimal.valueOf(Vat.VAT_9.getRate()), 2, RoundingMode.HALF_DOWN));
  }

  public BigDecimal getFinalIncomeTax(Company company) {
    return (getIncomeTax(company)
        .subtract(getReducedHealthInsurance(company)))
        .setScale(2, RoundingMode.HALF_UP);
  }

  public BigDecimal getRoundedFinalIncomeTax(Company company) {
    return getFinalIncomeTax(company)
        .setScale(0, RoundingMode.HALF_UP);
  }

  public TaxCalculatorResult calculateTaxes(Company company) {
    String taxIdNumber = company.getTaxIdentification();
    return TaxCalculatorResult.builder()
        .income(income(taxIdNumber))
        .costs(costs(taxIdNumber))
        .earnings(getEarnings(taxIdNumber))
        .pensionInsurance(company.getPensionInsurance())
        .earningsMinusPensionInsurance(getTaxBase(company))
        .earningsMinusPensionInsuranceRounded(getAboveRoundedTaxBase(company))
        .incomeTax(getIncomeTax(company))
        .healthInsurancePaid(company.getHealthInsurance())
        .healthInsuranceToSubtract(getReducedHealthInsurance(company))
        .incomeTaxMinusHealthInsurance(getFinalIncomeTax(company))
        .finalIncomeTax(getRoundedFinalIncomeTax(company))

        .collectedVat(collectedVat(taxIdNumber))
        .build();
  }
}
