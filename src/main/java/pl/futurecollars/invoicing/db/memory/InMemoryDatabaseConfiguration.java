package pl.futurecollars.invoicing.db.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "invoicing-system.database", havingValue = "memory")
public class InMemoryDatabaseConfiguration {

  @Bean
  public Database<Invoice> invoiceMemoryDatabase() {
    log.info("CURRENTLY THE APPLICATION WORKS WITH A MEMORY DATABASE");
    return new InMemoryDatabase<>();
  }

  @Bean
  public Database<Company> companyMemoryDatabase() {
    log.info("CURRENTLY THE APPLICATION WORKS WITH A MEMORY DATABASE");
    return new InMemoryDatabase<>();
  }
}
