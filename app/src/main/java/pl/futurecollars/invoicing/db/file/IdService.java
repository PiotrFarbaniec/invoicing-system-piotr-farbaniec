package pl.futurecollars.invoicing.db.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.Data;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.utils.FileService;
import pl.futurecollars.invoicing.utils.JsonService;

@Data
public class IdService {

  private final FileService fileService;
  private final JsonService jsonService;
  private final PathProvider provider;
  private final Path idPath;
  private final Path invoicePath;

  public IdService(FileService fileService, JsonService jsonService, PathProvider provider) {
    this.fileService = fileService;
    this.jsonService = jsonService;
    this.provider = provider;
    this.idPath = provider.getIdPath();
    this.invoicePath = provider.getInvoicePath();
  }

  public int getNextIdAndIncrement() {
    Integer nextId = getLastId() + 1;
    validateIdFile();
    try {
      fileService.writeToFile(idPath, String.valueOf(nextId));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return nextId;
  }

  private int getLastId() {
    File invoiceFile = new File(String.valueOf(invoicePath));
    List<String> list;
    int size;
    try {
      list = fileService.readAllLines(invoicePath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    size = !invoiceFile.exists() || list.isEmpty() ? 0 :
        (jsonService.toObject(list.get(list.size() - 1), Invoice.class)).getId();
    return size;
  }

  private void validateIdFile() {
    File idFile = new File(String.valueOf(idPath));
    if (!idFile.exists()) {
      try {
        Files.createFile(idPath);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
