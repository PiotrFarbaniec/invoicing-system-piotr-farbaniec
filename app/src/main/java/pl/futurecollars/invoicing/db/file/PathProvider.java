package pl.futurecollars.invoicing.db.file;

import java.nio.file.Path;
import lombok.Data;

@Data
public class PathProvider {

  private Path invoicePath;
  private Path idPath;

  public PathProvider(String fileName, String idPath) {
    this.invoicePath = Path.of(fileName);
    this.idPath = Path.of(idPath);
  }

  @Override
  public String toString() {
    return invoicePath.toString();
  }
}
