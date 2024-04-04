package pl.futurecollars.invoicing.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.InvoiceService;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

  private final InvoiceService service;

  @Autowired
  public InvoiceController(InvoiceService service) {
    this.service = service;
  }

  @GetMapping("/get/all")
  public ResponseEntity<?> getAllInvoices() {
    List<Invoice> invoicesList = service.getAll();
    if (invoicesList == null || invoicesList.size() == 0) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      return new ResponseEntity<>(invoicesList, HttpStatus.OK);
    }
  }

  @GetMapping("/get/{id}")
  public ResponseEntity<Invoice> getById(@PathVariable int id) {
    return service.getById(id)
        .map(invoice -> new ResponseEntity<Invoice>(invoice, HttpStatus.FOUND))
        .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
  }

  @PostMapping("/add/")
  public ResponseEntity<Integer> saveInvoice(@RequestBody Invoice invoice) {
    Integer added = service.save(invoice);
    return new ResponseEntity<Integer>(added, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateInvoice(@PathVariable int id, @RequestBody Invoice updatedInvoice) {
    Optional<Invoice> invoice = service.getById(id);
    if (invoice.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      service.update(id, updatedInvoice);
      return ResponseEntity.status(HttpStatus.OK).body(invoice.get());
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteInvoice(@PathVariable int id) {
    Optional<Invoice> invoice = service.getById(id);
    if (invoice.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      service.delete(id);
      return ResponseEntity.status(HttpStatus.OK).build();
    }
  }
}
