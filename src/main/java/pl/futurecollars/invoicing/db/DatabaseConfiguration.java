package pl.futurecollars.invoicing.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.futurecollars.invoicing.db.file.FileBasedDatabase;
import pl.futurecollars.invoicing.db.file.IdService;
import pl.futurecollars.invoicing.db.jpa.InvoiceRepository;
import pl.futurecollars.invoicing.db.jpa.JpaDatabase;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
import pl.futurecollars.invoicing.db.sql.SqlDatabase;
import pl.futurecollars.invoicing.utils.FileManager;
import pl.futurecollars.invoicing.utils.FileService;
import pl.futurecollars.invoicing.utils.JsonService;

@Slf4j
@Configuration
public class DatabaseConfiguration {

  @Bean
  public IdService idService(
      FileService fileService,
      @Value("${invoicing-system.database.directory}") String databaseDirectory,
      @Value("${invoicing-system.database.id.file}") String idFile) throws IOException {
    Path idFilePath = Files.createTempFile(databaseDirectory, idFile);
    return new IdService(fileService, idFilePath);
  }

  @Bean
  public Path databaseFilePath(
      @Value("${invoicing-system.database.directory}") String databaseDirectory,
      @Value("${invoicing-system.database.invoices.file}") String invoicesFile) throws IOException {
    return Files.createTempFile(databaseDirectory, invoicesFile);
  }

  @Bean
  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "file")
  public FileBasedDatabase fileDatabase(
      FileManager fileManager,
      FileService fileService,
      JsonService jsonService,
      @Value("${invoicing-system.database.directory}") String databaseDirectory,
      @Value("${invoicing-system.database.id.file}") String idFile,
      @Value("${invoicing-system.database.invoices.file}") String invoicesFile) throws IOException {
    log.info("CURRENTLY THE APPLICATION WORKS WITH A FILE DATABASE");
    return new FileBasedDatabase(
        fileManager,
        fileService,
        jsonService,
        idService(fileService, databaseDirectory, idFile),
        databaseFilePath(databaseDirectory, invoicesFile));
  }

  @Bean
  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "memory", matchIfMissing = true)
  public Database memoryDatabase() {
    log.info("CURRENTLY THE APPLICATION WORKS WITH A MEMORY DATABASE");
    return new InMemoryDatabase();
  }

  @Bean
  @ConditionalOnProperty(name = {"invoicing-system.database"}, havingValue = "sql")
  public Database sqlDatabase(JdbcTemplate jdbcTemplate) {
    log.info("CURRENTLY THE APPLICATION WORKS WITH AN SQL DATABASE");
    return new SqlDatabase(jdbcTemplate);
  }

  @Bean
  @ConditionalOnProperty(name = {"invoicing-system.database"}, havingValue = "jpa")
  public Database jpaDatabase(InvoiceRepository repository) {
    log.info("CURRENTLY THE APPLICATION WORKS WITH JPA DATABASE");
    return new JpaDatabase(repository);
  }
}
