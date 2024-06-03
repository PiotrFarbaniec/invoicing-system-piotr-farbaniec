package pl.futurecollars.invoicing.controller.company;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.futurecollars.invoicing.model.Company;

@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = {"/companies"}, produces = {"application/json;charset=UTF-8"})
@Api(tags = {"company-controller"})
public interface CompanyApi {

  @ApiOperation(value = "Download the list of all companies")
  @GetMapping(value = {"/get/all"})
  ResponseEntity<List<Company>> getAllCompanies();

  @ApiOperation(value = "Download a company by specific id /{number}")
  @GetMapping(value = {"/get/{id}"})
  ResponseEntity<Company> getCompanyById(@PathVariable("id") int id);

  @ApiOperation(value = "Save the new company in database /{content}")
  @PostMapping(value = {"/add/"})
  ResponseEntity<String> saveCompany(@RequestBody Company company);

  @ApiOperation(value = "Update a company with a specific id /{number}")
  @PutMapping(value = {"/update/{id}"})
  ResponseEntity<String> updateCompany(@PathVariable("id") int id, @RequestBody Company updatedCompany);

  @ApiOperation(value = "Delete a company with a specific id /{number}")
  @DeleteMapping(value = {"/delete/{id}"})
  ResponseEntity<String> deleteCompany(@PathVariable("id") int id);
}
