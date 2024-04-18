package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;

@Service
public class TaxCalculatorService {

  private final Database database;

  public TaxCalculatorService(Database database) {
    this.database = database;
  }

  public BigDecimal income(String taxIdNumber) {
    return database.visit(sellerPredicate(taxIdNumber), InvoiceEntry::getPrice);
  }

  public BigDecimal costs(String taxIdNumber) {
    return database.visit(buyerPredicate(taxIdNumber), InvoiceEntry::getPrice);
  }

  public BigDecimal incomingVat(String taxIdNumber) {
    return database.visit(sellerPredicate(taxIdNumber), InvoiceEntry::getVatValue);
  }

  public BigDecimal outgoingVat(String taxIdNumber) {
    return database.visit(buyerPredicate(taxIdNumber), InvoiceEntry::getVatValue);
  }

  public BigDecimal calculateEarnings(String taxIdNumber) {
    return income(taxIdNumber).subtract(costs(taxIdNumber));
  }

  public BigDecimal calculateVatToPay(String taxIdNumber) {
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
        .incomingVat(incomingVat(taxIdNumber))
        .outgoingVat(outgoingVat(taxIdNumber))
        .earnings(calculateEarnings(taxIdNumber))
        .vatToPay(calculateVatToPay(taxIdNumber))
        .build();
  }
}
