package pl.futurecollars.invoicing.db.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

@AllArgsConstructor
public class JpaDatabase implements Database {

  private final InvoiceRepository repository;

  @Override
  public int save(Invoice invoice) {
    return repository.save(invoice).getId();
  }

  @Override
  public Optional<Invoice> getById(int id) {
    return repository.findById(id);
  }

  @Override
  public List<Invoice> getAll() {
    return StreamSupport.stream(repository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void update(int id, Invoice updateInvoice) {
    Optional<Invoice> originalInvoice = getById(id);
    updateInvoice.setId(id);
    originalInvoice.ifPresent(item -> repository.save(updateInvoice));
  }

  @Override
  public void delete(int id) {
    Optional<Invoice> invoice = getById(id);
    invoice.ifPresent(repository::delete);
  }
}
