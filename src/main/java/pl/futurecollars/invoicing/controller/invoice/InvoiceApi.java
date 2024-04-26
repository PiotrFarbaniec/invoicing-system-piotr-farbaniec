package pl.futurecollars.invoicing.controller.invoice;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.futurecollars.invoicing.model.Invoice;

@RequestMapping(value = {"/invoices"})
@Api(tags = {"invoice-controller"})
public interface InvoiceApi {

  @ApiOperation(value = "Download list of all invoices")
  @GetMapping(value = {"/get/all"}, produces = {"application/json;charset=UTF-8"})
  ResponseEntity<List<Invoice>> getAllInvoices();

  @ApiOperation(value = "Download an invoice by specific id /{number}")
  @GetMapping(value = {"/get/{id}"}, produces = {"application/json;charset=UTF-8"})
  ResponseEntity<Invoice> getInvoiceById(@PathVariable("id") int id);

  @ApiOperation(value = "Save the new invoice in database /{content}")
  @PostMapping(value = {"/add/"}, produces = {"application/json;charset=UTF-8"})
  ResponseEntity<String> saveInvoice(@RequestBody Invoice invoice);

  @ApiOperation(value = "Update an invoice with a specific id /{number}")
  @PutMapping(value = {"/update/{id}"}, produces = {"application/json;charset=UTF-8"})
  ResponseEntity<String> updateInvoice(@PathVariable("id") int id, @RequestBody Invoice updatedInvoice);

  @ApiOperation(value = "Delete an invoice with a specific id /{number}")
  @DeleteMapping(value = {"/delete/{id}"}, produces = {"application/json;charset=UTF-8"})
  ResponseEntity<String> deleteInvoice(@PathVariable("id") int id);
}
