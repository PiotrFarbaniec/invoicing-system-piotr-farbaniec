package pl.futurecollars.invoicing.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FileService {

  public FileService() {
  }

  public void appendLineToFile(Path path, String line) throws IOException {
    ArgumentValidator.ensureArgumentNotNull(path, "path");
    ArgumentValidator.ensureArgumentNotNull(line, "line");
    Files.write(path, (line + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
  }

  public void writeToFile(Path path, String line) throws IOException {
    ArgumentValidator.ensureArgumentNotNull(path, "path");
    ArgumentValidator.ensureArgumentNotNull(line, "line");
    Files.write(path, (line + System.lineSeparator()).getBytes(), StandardOpenOption.WRITE);
  }

  public void writeLinesToFile(Path path, List<String> invoices) {
    ArgumentValidator.ensureArgumentNotNull(path, "path");
    ArgumentValidator.ensureArgumentNotNull(invoices, "invoices");
    String fileName = String.valueOf(path.getFileName());
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
      for (String line : invoices) {
        writer.write(line + System.lineSeparator());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void appendLinesToFile(Path path, List<String> invoices) {
    ArgumentValidator.ensureArgumentNotNull(path, "path");
    ArgumentValidator.ensureArgumentNotNull(invoices, "invoices");
    String fileName = String.valueOf(path.getFileName());
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
      for (String line : invoices) {
        writer.write(line + System.lineSeparator());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<String> readAllLines(Path path) throws IOException {
    ArgumentValidator.ensureArgumentNotNull(path, "path");
    return Files.readAllLines(path);
  }
}
