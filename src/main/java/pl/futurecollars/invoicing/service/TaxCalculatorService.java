package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Car;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;

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
    return database.visit(buyerPredicate(taxIdNumber), this::getVatValueTakingIntoConsiderationPersonalCarUse);
  }

  private BigDecimal getVatValueTakingIntoConsiderationPersonalCarUse(InvoiceEntry invoiceEntry) {
    return Optional.ofNullable(invoiceEntry.getCarRelatedExpenses())
        .map(Car::isUsedPrivately)
        .map(personalCarUsage -> personalCarUsage ? BigDecimal.valueOf(5, 1) : BigDecimal.ONE)
        .map(proportion -> invoiceEntry.getVatValue().multiply(proportion))
        .map(value -> value.setScale(2, RoundingMode.FLOOR))
        .orElse(invoiceEntry.getVatValue());
  }

  public BigDecimal incomingVat(String taxIdNumber) {
    return database.visit(sellerPredicate(taxIdNumber), InvoiceEntry::getVatValue);
  }

  public BigDecimal outgoingVat(String taxIdNumber) {
    return database.visit(buyerPredicate(taxIdNumber), InvoiceEntry::getVatValue);
  }

  public BigDecimal getEarnings(String taxIdNumber) {
    return income(taxIdNumber).subtract(costs(taxIdNumber));
  }

  public BigDecimal getVatToPay(String taxIdNumber) {
    return incomingVat(taxIdNumber).subtract(outgoingVat(taxIdNumber));
  }

  private Predicate<Invoice> sellerPredicate(String taxIdNumber) {
    return invoice -> taxIdNumber.equals(invoice.getSeller().getTaxIdentification());
  }

  private Predicate<Invoice> buyerPredicate(String taxIdNumber) {
    return invoice -> taxIdNumber.equals(invoice.getBuyer().getTaxIdentification());
  }

  public TaxCalculatorResult calculateTaxes(String taxIdNumber) {
    return TaxCalculatorResult.builder()
        .income(income(taxIdNumber))
        .costs(costs(taxIdNumber))
        .earnings(getEarnings(taxIdNumber))
        .collectedVat(incomingVat(taxIdNumber))
        .paidVat(outgoingVat(taxIdNumber))
        .earnings(getEarnings(taxIdNumber))
        .vatToPay(getVatToPay(taxIdNumber))
        .build();
  }
}
