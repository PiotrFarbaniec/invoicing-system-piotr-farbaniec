package pl.futurecollars.invoicing.db.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.Data;
import pl.futurecollars.invoicing.utils.FileService;

@Data
public class IdService {

  private final FileService fileService;
  private final Path idPath;

  public IdService(FileService fileService, Path idPath) {
    this.fileService = fileService;
    this.idPath = idPath;
  }

  public int getNextIdAndIncrement() {
    File idFile = new File(String.valueOf(idPath));
    validateIdFile(idFile);
    Integer nextId;
    List<String> fileContent;
    try {
      fileContent = fileService.readAllLines(idPath);
    } catch (IOException e) {
      throw new RuntimeException("Fail while reading ID file " + e.getMessage());
    }
    nextId = fileContent.isEmpty() ? 1 : Integer.parseInt(fileContent.get(0));
    try {
      fileService.writeToFile(idPath, String.valueOf(nextId + 1));
    } catch (IOException e) {
      throw new RuntimeException("Writing ID to file failed " + e.getMessage());
    }
    return nextId;
  }

  private void validateIdFile(File idFile) {
    if (!idFile.exists()) {
      try {
        Files.createFile(idPath);
      } catch (IOException e) {
        throw new RuntimeException("Create file with ID failed " + e.getMessage());
      }
    }
  }
}
