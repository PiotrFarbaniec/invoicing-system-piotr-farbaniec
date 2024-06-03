package pl.futurecollars.invoicing.controller.company;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.service.CompanyService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class CompanyController implements CompanyApi {

  @Autowired
  private final CompanyService service;

  @Autowired
  public CompanyController(CompanyService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<List<Company>> getAllCompanies() {
    Optional<List<Company>> companyList = Optional.ofNullable(service.getAll());
    return companyList.map(value -> ResponseEntity.status(HttpStatus.OK).body(value))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
  }

  @Override
  public ResponseEntity<Company> getCompanyById(@PathVariable int id) {
    Optional<Company> company = service.getById(id);
    return company.map(value -> ResponseEntity.status(HttpStatus.OK).body(value))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
  }

  @Override
  public ResponseEntity<String> saveCompany(@RequestBody Company company) {
    int added = service.save(company);
    return ResponseEntity.status(HttpStatus.CREATED).body(String.valueOf(added));
  }

  @Override
  public ResponseEntity<String> updateCompany(@PathVariable int id, @RequestBody Company updatedCompany) {
    Optional<Company> company = service.getById(id);
    return company.map(value -> {
      service.update(id, updatedCompany);
      return ResponseEntity.status(HttpStatus.OK).body("Company with ID: " + id + " has been successfully updated");
    })
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
  }

  @Override
  public ResponseEntity<String> deleteCompany(@PathVariable int id) {
    Optional<Company> company = service.getById(id);
    return company.map(value -> {
      service.delete(id);
      return ResponseEntity.status(HttpStatus.OK).body("Company with ID: " + id + " has been successfully removed");
    })
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
  }
}
