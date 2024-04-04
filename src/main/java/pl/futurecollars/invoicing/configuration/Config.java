package pl.futurecollars.invoicing.configuration;

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
  public PathProvider pathProvider() {
    return new PathProvider(INVOICES_FILE_NAME, ID_FILENAME);
  }

  @Bean
  public IdService idService(FileService fileService, JsonService jsonService, PathProvider provider) {
    return new IdService(fileService, jsonService, provider);
  }

  /*@Bean
  public FileService fileService() {
    return new FileService();
  }*/

  @Bean
  public FileManager fileManager() {
    return new FileManager();
  }

  @Bean
  public FileBasedDatabase fileBasedDatabase(FileManager fileManager, FileService fileService,
                                             JsonService jsonService, IdService idService,
                                             PathProvider provider) {
    return new FileBasedDatabase(fileManager, fileService, jsonService, idService, provider);
  }
}
