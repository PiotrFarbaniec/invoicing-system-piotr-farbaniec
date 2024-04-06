package pl.futurecollars.invoicing.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import lombok.Data;
import org.springframework.stereotype.Service;

@Data
@Service
public class FileManager {

  public FileManager() {
  }

  public void createFile(File file) throws IOException {
    if (file.exists()) {
      validateFileExistance(file, "Create of file operation failed!");
    }
    Files.createFile(file.toPath());
  }

  public void deleteFile(File file) throws IOException {
    Files.deleteIfExists(file.toPath());
  }

  public void validateFileExistance(File file, String message) throws FileNotFoundException {
    if (!file.exists()) {
      throw new FileNotFoundException(String
          .format("%s. File not exists. File path=[%s]", message, file.getAbsoluteFile()));
    }
  }

  public void copyFile(Path fromFilePath, Path toFilePath) throws IOException {
    Files.copy(fromFilePath, toFilePath, StandardCopyOption.REPLACE_EXISTING);
  }

  public void makeBackupFile(Path path) {
    File tempFile = new File(path.toFile().getParent(), String.format("[%s]_BACKUP.txt", path.getFileName()));
    try {
      createFile(tempFile);
      validateFileExistance(path.toFile(), "Fail while reading source file content");
      validateFileExistance(tempFile, "Fail while creating backup file");
      copyFile(path, Path.of(tempFile.getPath()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void deleteBackupFile(Path sourcePath) {
    File tempFile = new File(sourcePath.toFile().getParent(), String.format("[%s]_BACKUP.txt", sourcePath.getFileName()));
    try {
      validateFileExistance(sourcePath.toFile(), "Source file does not exist. Backup file has been saved");
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    try {
      deleteFile(tempFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
