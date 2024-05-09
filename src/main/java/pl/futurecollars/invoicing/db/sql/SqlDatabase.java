package pl.futurecollars.invoicing.db.sql;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;

@Slf4j
@AllArgsConstructor
public class SqlDatabase implements Database {

  private final JdbcTemplate jdbcTemplate;
  private final Map<Vat, Integer> vatToId = new HashMap<>();

  @Override
  public int save(Invoice invoice) {
    return 0;
  }

  @Override
  public Optional<Invoice> getById(int id) {
    return Optional.empty();
  }

  @Override
  public List<Invoice> getAll() {
    return null;
  }

  @Override
  public void update(int id, Invoice updateInvoice) {

  }

  @Override
  public void delete(int id) {

  }

  @Override
  public BigDecimal visit(Predicate<Invoice> invoicePredicate, Function<InvoiceEntry, BigDecimal> invoiceEntryToValue) {
    return Database.super.visit(invoicePredicate, invoiceEntryToValue);
  }
}
