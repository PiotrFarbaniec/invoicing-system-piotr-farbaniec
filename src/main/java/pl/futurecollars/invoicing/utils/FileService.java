package pl.futurecollars.invoicing.utils;

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

  public void writeLinesToFile(Path path, List<String> invoices) throws IOException {
    ArgumentValidator.ensureArgumentNotNull(path, "path");
    ArgumentValidator.ensureArgumentNotNull(invoices, "invoices");
    Files.write(path, invoices, StandardOpenOption.TRUNCATE_EXISTING);
  }

  public void appendLinesToFile(Path path, List<String> invoices) throws IOException {
    ArgumentValidator.ensureArgumentNotNull(path, "path");
    ArgumentValidator.ensureArgumentNotNull(invoices, "invoices");
    for (String line : invoices) {
      Files.write(path, (line + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
    }
  }

  public List<String> readAllLines(Path path) throws IOException {
    ArgumentValidator.ensureArgumentNotNull(path, "path");
    return Files.readAllLines(path);
  }
}
