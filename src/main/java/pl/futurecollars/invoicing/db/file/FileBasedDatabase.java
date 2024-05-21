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
import pl.futurecollars.invoicing.model.WithId;
import pl.futurecollars.invoicing.utils.FileManager;
import pl.futurecollars.invoicing.utils.FileService;
import pl.futurecollars.invoicing.utils.JsonService;

@Slf4j
public class FileBasedDatabase<T extends WithId> implements Database<T> {

  private final FileManager manager;
  private final FileService fileService;
  private final JsonService jsonService;
  private final IdService idService;
  private final Path itemPath;
  private final Class<T> clazz;

  public FileBasedDatabase(FileManager manager, FileService fileService,
                           JsonService jsonService, IdService idService,
                           Path itemPath, Class<T> clazz) {
    this.manager = manager;
    this.fileService = fileService;
    this.jsonService = jsonService;
    this.idService = idService;
    this.itemPath = itemPath;
    this.clazz = clazz;
  }

  @Override
  public int save(T item) {
    log.debug("Saving invoice: {}", jsonService.toJson(item));
    File invFile = new File(itemPath.toString());
    int nextId = idService.getNextIdAndIncrement();
    try {
      if (!invFile.exists()) {
        invFile.createNewFile();
        log.debug("The file: {} was successfully created", itemPath.getFileName());
      }
      item.setId(nextId);
      fileService.appendLineToFile(itemPath, jsonService.toJson(item));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    log.info("Invoice: {} successful saved in database", jsonService.toJson(item));
    return nextId;
  }

  @Override
  public Optional<T> getById(int id) {
    log.debug("Searching by id: {}", id);
    try {
      List<String> lines = fileService.readAllLines(itemPath);
      for (String line : lines) {
        T item = jsonService.toObject(line, clazz);
        if (item.getId() == id) {
          String objectType = item.getClass().getSimpleName();
          log.debug("The {} with id: {} has been found", objectType, id);
          return Optional.of(item);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    log.debug("Object with the specified id: {} was not found in the database", id);
    return Optional.empty();
  }

  @Override
  public List<T> getAll() {
    String objectType = clazz.getSimpleName();
    log.debug("Reading all {} from file: {}", objectType, itemPath.getFileName());
    try {
      return fileService.readAllLines(itemPath).stream()
          .map(line -> jsonService.toObject(line, clazz))
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void update(int id, T updateItem) {
    String objectType = clazz.getSimpleName();
    log.debug("Updating of {} with id: {}", objectType, id);
    manager.makeBackupFile(itemPath);
    try {
      List<String> lines = fileService.readAllLines(itemPath);
      List<String> updatedLines = new ArrayList<>();
      for (String line : lines) {
        if (isContainId(line, id)) {
          updateItem.setId(id);
          String updatedLine = jsonService.toJson(updateItem);
          updatedLines.add(updatedLine);
        } else {
          updatedLines.add(line);
        }
      }
      fileService.writeLinesToFile(itemPath, updatedLines);
      log.info("{} updating operation completed", objectType);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    manager.deleteBackupFile(itemPath);
  }

  @Override
  public void delete(int id) {
    String objectType = clazz.getSimpleName();
    log.debug("Deleting of {} with id: {}", objectType, id);
    Map<Integer, T> itemsMap = new HashMap<>();
    manager.makeBackupFile(itemPath);
    try {
      List<String> invoicesList = fileService.readAllLines(itemPath);
      for (String invoiceString : invoicesList) {
        T item = jsonService.toObject(invoiceString, clazz);
        itemsMap.put(item.getId(), item);
      }
      itemsMap.remove(id);
      saveInvoicesToFile(itemsMap.values());
      log.info("Invoice deleting operation completed");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    manager.deleteBackupFile(itemPath);
  }

  private void saveInvoicesToFile(Collection<T> items) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(itemPath.toFile()))) {
      for (T item : items) {
        String invoiceString = jsonService.toJson(item);
        writer.write(invoiceString);
        writer.newLine();
      }
    }
  }

  private boolean isContainId(String line, int id) {
    return line.contains("{\"id\":" + id + ",");
  }
}
