package pl.futurecollars.invoicing.db.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.WithId;

@AllArgsConstructor
public class MongoBasedDatabase<T extends WithId> implements Database<T> {

  private MongoCollection<T> invoiceCollection;
  private MongoIdProvider idProvider;

  @Override
  public int save(T item) {
    item.setId(idProvider.getNextIdAndIncrement().intValue());
    invoiceCollection.insertOne(item);
    return item.getId();
  }

  @Override
  public Optional<T> getById(int id) {
    return Optional.ofNullable(invoiceCollection.find(Filters.eq("_id", id)).first());
  }

  @Override
  public List<T> getAll() {
    return StreamSupport
        .stream(invoiceCollection.find().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void update(int id, T updateItem) {
    if (getById(id).isPresent()) {
      updateItem.setId(id);
      invoiceCollection.findOneAndReplace(Filters.eq("_id", id), updateItem);
    }
  }

  @Override
  public void delete(int id) {
    Optional<T> removedItem = getById(id);
    if (removedItem.isPresent()) {
      invoiceCollection.deleteOne(Filters.eq("_id", id));
    }
  }
}
