package pl.futurecollars.invoicing.db.sql;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Car;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;

@Slf4j
@AllArgsConstructor
public class CompanySqlDatabase  implements Database<Company> {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public int save(Company item) {
    return 0;
  }

  @Override
  public Optional<Company> getById(int id) {
    return Optional.empty();
  }

  @Override
  public List<Company> getAll() {
    return null;
  }

  @Override
  public void update(int id, Company updateItem) {

  }

  @Override
  public void delete(int id) {

  }
}
