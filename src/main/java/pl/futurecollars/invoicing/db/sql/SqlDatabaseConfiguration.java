package pl.futurecollars.invoicing.db.sql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;

@Slf4j
@Configuration
@ConditionalOnProperty(name = {"invoicing-system.database"}, havingValue = "sql")
public class SqlDatabaseConfiguration {

  @Bean
  public Database<Invoice> invoiceSqlDatabase(JdbcTemplate jdbcTemplate) {
    log.info("CURRENTLY THE APPLICATION WORKS WITH AN SQL DATABASE");
    return new InvoiceSqlDatabase(jdbcTemplate);
  }

  @Bean
  public Database<Company> companySqlDatabase(JdbcTemplate jdbcTemplate) {
    log.info("CURRENTLY THE APPLICATION WORKS WITH AN SQL DATABASE");
    return new CompanySqlDatabase(jdbcTemplate);
  }
}
