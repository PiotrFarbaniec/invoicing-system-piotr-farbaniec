package pl.futurecollars.invoicing.controller.tax;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.service.TaxCalculatorResult;
import pl.futurecollars.invoicing.service.TaxCalculatorService;

@RestController
public class TaxCalculatorController implements TaxCalculatorApi {

  @Autowired
  private final TaxCalculatorService taxCalculatorService;

  @Autowired
  public TaxCalculatorController(TaxCalculatorService taxCalculatorService) {
    this.taxCalculatorService = taxCalculatorService;
  }

  @Override
  public ResponseEntity<TaxCalculatorResult> calulateTaxes(@RequestBody Company company) {
    return ResponseEntity.status(HttpStatus.OK).body(taxCalculatorService.calculateTaxes(company));
  }
}
