package pl.futurecollars.invoicing.controller.tax;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.futurecollars.invoicing.service.TaxCalculatorResult;

@RequestMapping(value = {"/tax"})
@Api(tags = {"tax-controller"})
public interface TaxCalculatorApi {

  /* @ApiOperation(value = "Download tax calculations by specified identification tax number")
  @GetMapping(value = {"/{taxIdNumber}"}, produces = {"application/json;charset=UTF-8"})
  @ResponseStatus(HttpStatus.OK)
  TaxCalculatorResult calculateTaxes(@PathVariable @ApiParam(example = "555-444-33-22") String taxIdNumber); */

  @ApiOperation(value = "Download tax calculations by specified identification tax number")
  @GetMapping(value = {"/{taxIdNumber}"}, produces = {"application/json;charset=UTF-8"})
  ResponseEntity<TaxCalculatorResult> calulateTaxes(@PathVariable @ApiParam(example = "555-444-33-22") String taxIdNumber);
}
