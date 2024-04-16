package pl.futurecollars.invoicing.controller.invoice;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.InvoiceService;

@RestController
public class InvoiceController implements InvoiceApi {

  @Autowired
  private final InvoiceService service;

  @Autowired
  public InvoiceController(InvoiceService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<List<Invoice>> getAllInvoices() {
    List<Invoice> invoicesList = service.getAll();
    if (invoicesList == null || invoicesList.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      return ResponseEntity.status(HttpStatus.OK).body(invoicesList);
    }
  }

  @Override
  public ResponseEntity<Invoice> getInvoiceById(@PathVariable int id) {
    Optional<Invoice> invoice = service.getById(id);
    return invoice.map(value -> ResponseEntity.status(HttpStatus.OK).body(value))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
  }

  @Override
  public ResponseEntity<String> saveInvoice(@RequestBody Invoice invoice) {
    int added = service.save(invoice);
    return ResponseEntity.status(HttpStatus.CREATED).body("Invoice with ID: " + added + " has been successfully saved");
  }

  @Override
  public ResponseEntity<String> updateInvoice(@PathVariable int id, @RequestBody Invoice updatedInvoice) {
    Optional<Invoice> invoice = service.getById(id);
    if (invoice.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      service.update(id, updatedInvoice);
      return ResponseEntity.status(HttpStatus.OK).body("Invoice with ID: " + id + " has been successfully updated");
    }
  }

  @Override
  public ResponseEntity<String> deleteInvoice(@PathVariable int id) {
    Optional<Invoice> invoice = service.getById(id);
    if (invoice.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      service.delete(id);
      return ResponseEntity.status(HttpStatus.OK).body("Invoice with ID: " + id + " has been successfully removed");
    }
  }
}
