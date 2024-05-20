package pl.futurecollars.invoicing.db.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.WithId;

@Slf4j
public class InMemoryDatabase<T extends WithId> implements Database<T> {

  private final Map<Integer, T> items = new HashMap<>();
  private int nextId = 1;

  @Override
  public int save(T item) {
    item.setId(nextId);
    items.put(nextId, item);
    String objectName = item.getClass().getSimpleName();
    log.debug("{} with id: {} successful saved in database", objectName, nextId);
    return nextId++;
  }

  @Override
  public Optional<T> getById(int id) {
    return Optional.ofNullable(items.get(id));
  }

  @Override
  public void update(int id, T updateItem) {
    if (items.containsKey(id)) {
      updateItem.setId(id);
      items.put(id, updateItem);
      String objectName = updateItem.getClass().getSimpleName();
      log.debug("{} with id: {} successful updated", objectName, id);
    }
    String objectName = updateItem.getClass().getSimpleName();
    log.debug("No {} with the specified id: {} in database", objectName, id);
  }

  @Override
  public void delete(int id) {
    if (items.containsKey(id)) {
      items.remove(id);
      String objectName = items.values().getClass().getSimpleName();
      log.debug("{} with id: {} successful deleted", objectName, id);
    }
    String objectName = items.values().getClass().getSimpleName();
    log.debug("No {} with the specified id: {} in database", objectName, id);
  }

  @Override
  public List<T> getAll() {
    return new ArrayList<>(items.values());
  }
}
