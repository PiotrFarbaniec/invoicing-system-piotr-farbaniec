package pl.futurecollars.invoicing.db.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.WithId;

@AllArgsConstructor
public class JpaDatabase<T extends WithId> implements Database<T> {

  private final CrudRepository<T, Integer> repository;

  @Override
  public int save(T item) {
    return repository.save(item).getId();
  }

  @Override
  public Optional<T> getById(int id) {
    return repository.findById(id);
  }

  @Override
  public List<T> getAll() {
    return StreamSupport.stream(repository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void update(int id, T updateItem) {
    Optional<T> originalItem = getById(id);
    updateItem.setId(id);
    originalItem.ifPresent(item -> repository.save(updateItem));
  }

  @Override
  public void delete(int id) {
    Optional<T> invoice = getById(id);
    invoice.ifPresent(repository::delete);
  }
}
