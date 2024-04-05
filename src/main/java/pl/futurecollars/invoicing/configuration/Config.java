package pl.futurecollars.invoicing.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.file.FileBasedDatabase;
import pl.futurecollars.invoicing.db.file.IdService;
import pl.futurecollars.invoicing.db.file.PathProvider;
import pl.futurecollars.invoicing.utils.FileManager;
import pl.futurecollars.invoicing.utils.FileService;
import pl.futurecollars.invoicing.utils.JsonService;

@Configuration
public class Config {
  public static final String DATABASE_LOCATION = "db";
  public static final String ID_FILENAME = "idRecord.txt";
  public static final String INVOICES_FILE_NAME = "invoicesRecord.txt";

  @Bean
  public PathProvider pathProviderBean() {
    return new PathProvider(INVOICES_FILE_NAME, ID_FILENAME);
  }

  @Bean
  public Path idFilePath() throws IOException {
    return Files.createTempFile(DATABASE_LOCATION, ID_FILENAME); // potrzebny ???
  }

  @Bean
  public Path invoiceFilePath() throws IOException {
    return Files.createTempFile(DATABASE_LOCATION, INVOICES_FILE_NAME); // potrzebny ???
  }

  @Bean
  public IdService idServiceBean(FileService fileService, JsonService jsonService, PathProvider provider) {
    return new IdService(fileService, jsonService, provider);
  }

  @Bean
  public FileService fileServiceBean() {
    return new FileService();
  }

  /*  @Bean
  public FileManager fileManagerBean() {
    return new FileManager();
  } */

  @Bean
  public FileBasedDatabase fileDatabase(FileManager fileManager, FileService fileService,
                                             JsonService jsonService, IdService idService,
                                             PathProvider provider) {
    return new FileBasedDatabase(fileManager, fileService, jsonService, idService, provider);
  }
}
