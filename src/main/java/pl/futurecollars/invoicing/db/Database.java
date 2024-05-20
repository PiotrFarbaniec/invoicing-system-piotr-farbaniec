package pl.futurecollars.invoicing.db;

import java.util.List;
import java.util.Optional;

public interface Database<T> {

  int save(T item);

  Optional<T> getById(int id);

  List<T> getAll();

  void update(int id, T updateItem);

  void delete(int id);
}
