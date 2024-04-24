package pl.futurecollars.invoicing.controller.tax;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.service.TaxCalculatorResult;

@RequestMapping(value = {"/tax"})
@Api(tags = {"tax-controller"})
public interface TaxCalculatorApi {


  @ApiOperation(value = "Get tax calculations according to specific company /{content}")
  @PostMapping(value = {"/company/"}, produces = {"application/json;charset=UTF-8"})
  ResponseEntity<TaxCalculatorResult> calulateTaxes(@RequestBody Company company);




}
