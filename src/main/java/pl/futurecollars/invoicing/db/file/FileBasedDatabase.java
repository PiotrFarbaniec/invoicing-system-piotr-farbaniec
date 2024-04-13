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
import lombok.extern.slf4j.Slf4j;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.utils.FileManager;
import pl.futurecollars.invoicing.utils.FileService;
import pl.futurecollars.invoicing.utils.JsonService;

@Slf4j
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
    log.debug("Saving invoice: {}", jsonService.toJson(invoice));
    File invFile = new File(invoicePath.toString());
    int nextId = idService.getNextIdAndIncrement();
    try {
      if (!invFile.exists()) {
        invFile.createNewFile();
        log.debug("The invoice file: {} was successfully created", invoicePath.getFileName());
      }
      invoice.setId(nextId);
      fileService.appendLineToFile(invoicePath, jsonService.toJson(invoice));
    } catch (IOException e) {
      log.error("Recording an invoice: {} to the database failed with exception {}", jsonService.toJson(invoice), e.getMessage(), e);
      throw new RuntimeException(e);
    }
    log.info("Invoice: {} successful saved in database", jsonService.toJson(invoice));
    return nextId;
  }

  @Override
  public Optional<Invoice> getById(int id) {
    log.debug("Searching an invoice by id: {}", id);
    try {
      List<String> lines = fileService.readAllLines(invoicePath);
      for (String line : lines) {
        Invoice invoice = jsonService.toObject(line, Invoice.class);
        if (invoice.getId() == id) {
          log.info("The invoice with id: {} has been found", id);
          return Optional.of(invoice);
        }
      }
    } catch (IOException e) {
      log.error("Reading invoice file: {} failed with exception {}", invoicePath.getFileName(), e.getMessage(), e);
      throw new RuntimeException(e);
    }
    log.debug("Invoice with the specified id: {} was not found in the database", id);
    return Optional.empty();
  }

  @Override
  public List<Invoice> getAll() {
    log.debug("Reading all invoices from file: {}", invoicePath.getFileName());
    try {
      return fileService.readAllLines(invoicePath).stream()
          .map(line -> jsonService.toObject(line, Invoice.class))
          .collect(Collectors.toList());
    } catch (IOException e) {
      log.error("Reading invoice file: {} failed with exception {}", invoicePath.getFileName(), e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  public void update(int id, Invoice updatedInvoice) {
    log.debug("Updating an invoice with id: {}", id);
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
      log.info("Invoice updating operation completed");
    } catch (IOException e) {
      log.error("Writing/reading file: {} while updating failed with exception {}", invoicePath.getFileName(), e.getMessage(), e);
      throw new RuntimeException(e);
    }
    manager.deleteBackupFile(invoicePath);
  }

  @Override
  public void delete(int id) {
    log.debug("Deleting an invoice with id: {}", id);
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
      log.info("Invoice deleting operation completed");
    } catch (IOException e) {
      log.error("Writing/reading file: {} while deleting invoice {} failed with exception {}", invoicePath.getFileName(), id, e.getMessage(), e);
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
