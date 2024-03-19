package pl.futurecollars.invoicing.model;

import java.time.LocalDate;
import lombok.Data;

@Data
public class Invoice {

  private int id;
  private LocalDate date;
  private Company buyer;
  private Company seller;
  private InvoiceEntry invoiceEntry;

  public Invoice(int id, LocalDate date, Company buyer, Company seller, InvoiceEntry invoiceEntry) {
    this.id = id;
    this.date = date;
    this.buyer = buyer;
    this.seller = seller;
    this.invoiceEntry = invoiceEntry;
  }
}
