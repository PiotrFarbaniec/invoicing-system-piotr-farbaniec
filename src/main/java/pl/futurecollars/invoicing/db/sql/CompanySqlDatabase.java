package pl.futurecollars.invoicing.db.sql;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;

@Slf4j
@AllArgsConstructor
public class CompanySqlDatabase implements Database<Company> {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public int save(Company company) {
    GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
    String sqlStatement = """
        INSERT INTO public.company
        (tax_identification, address, name, pension_insurance, health_insurance)
        values (?, ?, ?, ?, ?);""";
    jdbcTemplate.update(connection -> {
      PreparedStatement statement = connection.prepareStatement(sqlStatement, new String[] {"id"});
      statement.setString(1, company.getTaxIdentification());
      statement.setString(2, company.getAddress());
      statement.setString(3, company.getName());
      statement.setBigDecimal(4, company.getPensionInsurance());
      statement.setBigDecimal(5, company.getHealthInsurance());
      return statement;
    }, generatedKeyHolder);
    return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
  }

  @Override
  public Optional<Company> getById(int id) {
    String sqlStatement = """
        SELECT * FROM public.company
        WHERE id = ?""";
    List<Company> company = jdbcTemplate.query(sqlStatement, (rs, rowNum) -> Company.builder()
        .id(rs.getInt("id"))
        .taxIdentification(rs.getString("tax_identification"))
        .address(rs.getString("address"))
        .name(rs.getString("name"))
        .pensionInsurance(rs.getBigDecimal("pension_insurance"))
        .healthInsurance(rs.getBigDecimal("health_insurance"))
        .build(), id);
    return company.isEmpty() ? Optional.empty() : Optional.of(company.get(0));
  }

  @Override
  public List<Company> getAll() {
    String sqlStatement = "SELECT * FROM public.company";
    return jdbcTemplate.query(sqlStatement, (rs, rowNumber) -> Company.builder()
        .id(rs.getInt("id"))
        .taxIdentification(rs.getString("tax_identification"))
        .address(rs.getString("address"))
        .name(rs.getString("name"))
        .pensionInsurance(rs.getBigDecimal("pension_insurance"))
        .healthInsurance(rs.getBigDecimal("health_insurance"))
        .build());
  }

  @Override
  @Transactional
  public void update(int id, Company updateCompany) {
    String sqlUpdate = """
        UPDATE public.company SET
        tax_identification = ?, address = ?, name = ?,
        pension_insurance = ?, health_insurance = ?
        WHERE id = ?""";
    if (getById(id).isPresent()) {
      Company originalCompany = getById(id).get();
      Integer originalId = originalCompany.getId();
      updateCompany.setId(originalId);
      jdbcTemplate.update(sqlUpdate, ps -> {
        ps.setString(1, updateCompany.getTaxIdentification());
        ps.setString(2, updateCompany.getAddress());
        ps.setString(3, updateCompany.getName());
        ps.setBigDecimal(4, updateCompany.getPensionInsurance());
        ps.setBigDecimal(5, updateCompany.getHealthInsurance());
        ps.setInt(6, originalId);
      });
    }
  }

  @Override
  @Transactional
  public void delete(int id) {
    String sqlDelete = "DELETE FROM public.company WHERE id = ?";
    if (getById(id).isPresent()) {
      jdbcTemplate.update(sqlDelete, id);
    }
  }
}
