package pl.futurecollars.invoicing.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "invoice-controller")
public class InvoiceController {

  @Autowired
  private final InvoiceService service;

  @Autowired
  public InvoiceController(InvoiceService service) {
    this.service = service;
  }

  @ApiOperation(value = "Download list of all invoices")
  @GetMapping(value = "/get/all", produces = "application/json;charset=UTF-8")
  public ResponseEntity<List<Invoice>> getAllInvoices() {
    List<Invoice> invoicesList = service.getAll();
    if (invoicesList == null || invoicesList.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      return ResponseEntity.status(HttpStatus.OK).body(invoicesList);
    }
  }

  @ApiOperation(value = "Download an invoice by specific id /{number}")
  @GetMapping(value = "/get/{id}", produces = "application/json;charset=UTF-8")
  public ResponseEntity<Invoice> getInvoiceById(@PathVariable int id) {
    Optional<Invoice> invoice = service.getById(id);
    return invoice.map(value -> ResponseEntity.status(HttpStatus.OK).body(value))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
  }

  @ApiOperation(value = "Save the new invoice in database /{content}")
  @PostMapping(value = "/add/", produces = "application/json;charset=UTF-8")
  public ResponseEntity<String> saveInvoice(@RequestBody Invoice invoice) {
    int added = service.save(invoice);
    return ResponseEntity.status(HttpStatus.CREATED).body("Invoice with ID: " + added + " has been successfully saved");
  }

  @ApiOperation(value = "Update an invoice with a specific id /{number}")
  @PutMapping(value = "/update/{id}", produces = "application/json;charset=UTF-8")
  public ResponseEntity<String> updateInvoice(@PathVariable int id, @RequestBody Invoice updatedInvoice) {
    Optional<Invoice> invoice = service.getById(id);
    if (invoice.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      service.update(id, updatedInvoice);
      return ResponseEntity.status(HttpStatus.OK).body("Invoice with ID: " + id + " has been successfully updated");
    }
  }

  @ApiOperation(value = "Delete an invoice with a specific id /{number}")
  @DeleteMapping(value = "/delete/{id}", produces = "application/json;charset=UTF-8")
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
