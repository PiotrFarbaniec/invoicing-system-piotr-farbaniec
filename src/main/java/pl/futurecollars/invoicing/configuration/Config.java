package pl.futurecollars.invoicing.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.file.FileBasedDatabase;
import pl.futurecollars.invoicing.db.file.IdService;
import pl.futurecollars.invoicing.utils.FileManager;
import pl.futurecollars.invoicing.utils.FileService;
import pl.futurecollars.invoicing.utils.JsonService;

@Configuration
public class Config {
  public static final String DATABASE_LOCATION = "db";
  public static final String ID_FILENAME = "ID.txt";
  public static final String INVOICES_FILE_NAME = "INVOICES.txt";

  @Bean
  public IdService idServiceBean(FileService fileService) throws IOException {
    Path idPath = Files.createTempFile(DATABASE_LOCATION, ID_FILENAME);
    return new IdService(fileService, idPath);
  }

  @Bean
  public FileBasedDatabase fileDatabase(FileManager fileManager, FileService fileService,
                                             JsonService jsonService, IdService idService) throws IOException {
    Path dtabaseFilePath = Files.createTempFile(DATABASE_LOCATION, INVOICES_FILE_NAME);
    return new FileBasedDatabase(fileManager, fileService, jsonService, idService, dtabaseFilePath);
  }
}
