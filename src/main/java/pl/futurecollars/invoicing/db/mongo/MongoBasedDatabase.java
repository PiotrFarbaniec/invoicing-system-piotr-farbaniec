package pl.futurecollars.invoicing.db.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

@AllArgsConstructor
public class MongoBasedDatabase implements Database<Invoice> {

  private MongoCollection<Invoice> invoiceCollection;
  private MongoIdProvider idProvider;

  @Override
  public int save(Invoice invoice) {
    invoice.setId(idProvider.getNextIdAndIncrement().intValue());
    invoiceCollection.insertOne(invoice);
    return invoice.getId();
  }

  @Override
  public Optional<Invoice> getById(int id) {
    return Optional.ofNullable(invoiceCollection.find(Filters.eq("_id", id)).first());
  }

  @Override
  public List<Invoice> getAll() {
    return StreamSupport
        .stream(invoiceCollection.find().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void update(int id, Invoice updateInvoice) {
    if (getById(id).isPresent()) {
      updateInvoice.setId(id);
      invoiceCollection.findOneAndReplace(Filters.eq("_id", id), updateInvoice);
    }
  }

  @Override
  public void delete(int id) {
    Optional<Invoice> removedInvoice = getById(id);
    if (removedInvoice.isPresent()) {
      invoiceCollection.deleteOne(Filters.eq("_id", id));
    }
  }
}
