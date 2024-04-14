package pl.futurecollars.invoicing.db.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

@Slf4j
public class InMemoryDatabase implements Database {

  private final Map<Integer, Invoice> invoices = new HashMap<>();
  private int nextId = 1;

  @Override
  public int save(Invoice invoice) {
    invoice.setId(nextId);
    invoices.put(nextId, invoice);
    log.info("Invoice: {} successful saved in database", invoice);
    return nextId++;
  }

  @Override
  public Optional<Invoice> getById(int id) {
    return Optional.ofNullable(invoices.get(id));
  }

  @Override
  public void update(int id, Invoice updateInvoice) {
    if (invoices.containsKey(id)) {
      updateInvoice.setId(id);
      invoices.put(id, updateInvoice);
      log.info("Invoice with id: {} successful updated", id);
    }
    log.info("No invoice with the specified id: {} in database", id);
  }

  @Override
  public void delete(int id) {
    if (invoices.containsKey(id)) {
      invoices.remove(id);
      log.info("Invoice with id: {} successful deleted", id);
    }
    log.info("No invoice with the specified id: {} in database", id);
  }

  @Override
  public List<Invoice> getAll() {
    return new ArrayList<>(invoices.values());
  }
}
