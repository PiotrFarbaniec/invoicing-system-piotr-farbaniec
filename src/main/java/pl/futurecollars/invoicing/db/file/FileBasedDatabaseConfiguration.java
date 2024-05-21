package pl.futurecollars.invoicing.db.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.utils.FileManager;
import pl.futurecollars.invoicing.utils.FileService;
import pl.futurecollars.invoicing.utils.JsonService;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "invoicing-system.database", havingValue = "file")
public class FileBasedDatabaseConfiguration {

  @Bean
  public IdService invoiceIdService(
      FileService fileService,
      @Value("${invoicing-system.database.directory}") String databaseDirectory,
      @Value("${invoicing-system.database.invoice_id.file}") String idFile) throws IOException {
    Path idFilePath = Files.createTempFile(databaseDirectory, idFile);
    return new IdService(fileService, idFilePath);
  }

  @Bean
  public IdService companyIdService(
      FileService fileService,
      @Value("${invoicing-system.database.directory}") String databaseDirectory,
      @Value("${invoicing-system.database.company_id.file}") String idFile) throws IOException {
    Path idFilePath = Files.createTempFile(databaseDirectory, idFile);
    return new IdService(fileService, idFilePath);
  }

  @Bean
  public Path invoiceFilePath(
      @Value("${invoicing-system.database.directory}") String databaseDirectory,
      @Value("${invoicing-system.database.invoices.file}") String invoicesFile) throws IOException {
    return Files.createTempFile(databaseDirectory, invoicesFile);
  }

  @Bean
  public Path companyFilePath(
      @Value("${invoicing-system.database.directory}") String databaseDirectory,
      @Value("${invoicing-system.database.companies.file}") String invoicesFile) throws IOException {
    return Files.createTempFile(databaseDirectory, invoicesFile);
  }

  @Bean
  public Database<Invoice> invoiceFileBasedDatabase(
      FileManager fileManager,
      FileService fileService,
      JsonService jsonService,
      @Value("${invoicing-system.database.directory}") String databaseDirectory,
      @Value("${invoicing-system.database.invoice_id.file}") String invoiceIdFile,
      @Value("${invoicing-system.database.invoices.file}") String invoicesFile) throws IOException {
    log.info("CURRENTLY THE APPLICATION WORKS WITH A FILE DATABASE");
    return new FileBasedDatabase<>(
        fileManager,
        fileService,
        jsonService,
        invoiceIdService(fileService, databaseDirectory, invoiceIdFile),

        invoiceFilePath(databaseDirectory, invoicesFile), Invoice.class);
  }

  @Bean
  public Database<Company> companyFileBasedDatabase(
      FileManager fileManager,
      FileService fileService,
      JsonService jsonService,
      @Value("${invoicing-system.database.directory}") String databaseDirectory,
      @Value("${invoicing-system.database.company_id.file}") String companyIdFile,
      @Value("${invoicing-system.database.companies.file}") String companiesFile) throws IOException {
    log.info("CURRENTLY THE APPLICATION WORKS WITH A FILE DATABASE");
    return new FileBasedDatabase<>(
        fileManager,
        fileService,
        jsonService,
        companyIdService(fileService, databaseDirectory, companyIdFile),
        companyFilePath(databaseDirectory, companiesFile), Company.class);
  }
}
