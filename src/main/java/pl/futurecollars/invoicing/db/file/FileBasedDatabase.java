package pl.futurecollars.invoicing.db.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.utils.FileManager;
import pl.futurecollars.invoicing.utils.FileService;
import pl.futurecollars.invoicing.utils.JsonService;

@Repository
public class FileBasedDatabase implements Database {

  private final FileManager manager;
  private final FileService fileService;
  private final JsonService jsonService;
  private final IdService idService;
  private final Path invoicePath;

  public FileBasedDatabase(FileManager manager, FileService fileService,
                           JsonService jsonService, IdService idService,
                           Path invoicePath) {
    this.manager = manager;
    this.fileService = fileService;
    this.jsonService = jsonService;
    this.idService = idService;
    this.invoicePath = invoicePath;
  }

  @Override
  public int save(Invoice invoice) {
    File invFile = new File(invoicePath.toString());
    int nextId = idService.getNextIdAndIncrement();
    try {
      if (!invFile.exists()) {
        invFile.createNewFile();
      }
      invoice.setId(nextId /* idService.getNextIdAndIncrement() */);
      fileService.appendLineToFile(invoicePath, jsonService.toJson(invoice));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return nextId /* idService.getNextIdAndIncrement() */;
  }

  @Override
  public Optional<Invoice> getById(int id) {
    try {
      List<String> lines = fileService.readAllLines(invoicePath);
      for (String line : lines) {
        Invoice invoice = jsonService.toObject(line, Invoice.class);
        if (invoice.getId() == id) {
          return Optional.of(invoice);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return Optional.empty();
  }

  @Override
  public List<Invoice> getAll() {
    try {
      return fileService.readAllLines(invoicePath).stream()
          .map(line -> jsonService.toObject(line, Invoice.class))
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void update(int id, Invoice updatedInvoice) {
    manager.makeBackupFile(invoicePath);
    try {
      List<String> lines = fileService.readAllLines(invoicePath);
      List<String> updatedLines = new ArrayList<>();
      for (String line : lines) {
        if (isContainId(line, id)) {
          updatedInvoice.setId(id);
          String updatedLine = jsonService.toJson(updatedInvoice);
          updatedLines.add(updatedLine);
        } else {
          updatedLines.add(line);
        }
      }
      fileService.writeLinesToFile(invoicePath, updatedLines);
    } catch (IOException e) {
      throw new RuntimeException("Invoice updating fail", e);
    }
    manager.deleteBackupFile(invoicePath);
  }

  @Override
  public void delete(int id) {
    Map<Integer, Invoice> invoices = new HashMap<>();
    manager.makeBackupFile(invoicePath);
    try {
      List<String> invoicesList = fileService.readAllLines(invoicePath);
      for (String invoiceString : invoicesList) {
        Invoice invoice = jsonService.toObject(invoiceString, Invoice.class);
        invoices.put(invoice.getId(), invoice);
      }
      invoices.remove(id);
      saveInvoicesToFile(invoices.values());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    manager.deleteBackupFile(invoicePath);
  }

  private void saveInvoicesToFile(Collection<Invoice> invoices) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(invoicePath.toFile()))) {
      for (Invoice invoice : invoices) {
        String invoiceString = jsonService.toJson(invoice);
        writer.write(invoiceString);
        writer.newLine();
      }
    }
  }

  private boolean isContainId(String line, int id) {
    return line.contains("{\"id\":" + id + ",");
  }
}
