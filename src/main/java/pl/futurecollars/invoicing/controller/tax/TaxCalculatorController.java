package pl.futurecollars.invoicing.controller.tax;

import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
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
  public ResponseEntity<TaxCalculatorResult> calulateTaxes(@PathVariable @ApiParam(example = "555-444-33-22") String taxIdNumber) {
    if (taxCalculatorService.calculateTaxes(taxIdNumber) == null) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      return ResponseEntity.status(HttpStatus.OK).body(taxCalculatorService.calculateTaxes(taxIdNumber));
    }
  }
}
