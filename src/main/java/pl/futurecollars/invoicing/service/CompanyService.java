package pl.futurecollars.invoicing.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;

@Service
public class CompanyService {

  private final Database<Company> database;

  public CompanyService(Database<Company> database) {
    this.database = database;
  }

  public int save(Company company) {
    return database.save(company);
  }

  public Optional<Company> getById(int id) {
    return database.getById(id);
  }

  public List<Company> getAll() {
    return database.getAll();
  }

  public void update(int id, Company updateCompany) {
    database.update(id, updateCompany);
  }

  public void delete(int id) {
    database.delete(id);
  }
}
