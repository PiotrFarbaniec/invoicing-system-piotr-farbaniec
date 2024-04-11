package pl.futurecollars.invoicing.model;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class InvoiceEntry {

  private String description;
  private int quantity;
  private BigDecimal price;
  private BigDecimal vatValue;
  private Vat vatRate;

  public InvoiceEntry() {
  }

  public InvoiceEntry(String description, int quantity, BigDecimal price, BigDecimal vatValue, Vat vatRate) {
    this.description = description;
    this.quantity = quantity;
    this.price = price;
    this.vatValue = vatValue;
    this.vatRate = vatRate;
  }
}
