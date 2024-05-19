package pl.futurecollars.invoicing.db.sql;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Car;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;

@Slf4j
@AllArgsConstructor
public class SqlDatabase implements Database {

  private final JdbcTemplate jdbcTemplate;
  private final Map<Vat, Integer> vatToId = new HashMap<>();

  @PostConstruct
  void initlizeVatMap() {
    jdbcTemplate.query("SELECT * FROM public.vat;", resultSet -> {
      vatToId.put(Vat.valueOf(Vat.class, resultSet.getString("name")), resultSet.getInt("id"));
    });
  }

  @Override
  public int save(Invoice invoice) {
    GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
    int buyerId = saveCompany(invoice.getBuyer());
    int sellerId = saveCompany(invoice.getSeller());
    String sqlStatement1 = """
        INSERT INTO public.invoices
        (number, date, buyer, seller) values (?, ?, ?, ?);""";
    String sqlStatement2 = """
        UPDATE public.invoices
        SET invoice_entry = ?
        WHERE id = ?;""";
    jdbcTemplate.update(connection -> {
      PreparedStatement statement = connection.prepareStatement(sqlStatement1, new String[] {"id"});
      statement.setString(1, invoice.getNumber());
      statement.setDate(2, Date.valueOf(invoice.getDate()));
      statement.setInt(3, buyerId);
      statement.setInt(4, sellerId);
      return statement;
    }, generatedKeyHolder);

    final int invoiceId = Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
    invoice.getEntries().stream().forEach(entry -> saveEntry(invoiceId, entry));
    jdbcTemplate.update(sqlStatement2, invoiceId, invoiceId);
    return invoiceId;
  }

  private int saveCompany(Company company) {
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

  private void saveEntry(int invoiceId, InvoiceEntry entry) {
    GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
    Optional<Integer> carId;
    String presentCarSql = """
        INSERT INTO public.invoice_entry
        (description, quantity, net_price, vat_value, vat_rate, car_related_expenses)
        values (?, ?, ?, ?, ?, ?);""";
    String emptyCarSql = """
        INSERT INTO public.invoice_entry
        (description, quantity, net_price, vat_value, vat_rate)
        values (?, ?, ?, ?, ?);""";

    if (Optional.ofNullable(entry.getCarRelatedExpenses()).isPresent()
        && !entry.getCarRelatedExpenses().getRegistrationNumber().isBlank()) {
      carId = saveCar(entry);
    } else {
      carId = Optional.empty();
    }
    if (carId.isPresent()) {
      jdbcTemplate.update(con -> {
        PreparedStatement statement = con.prepareStatement(presentCarSql, new String[] {"id"});
        statement.setString(1, entry.getDescription());
        statement.setInt(2, entry.getQuantity());
        statement.setBigDecimal(3, entry.getNetPrice());
        statement.setBigDecimal(4, entry.getVatValue());
        statement.setInt(5, vatToId.get(entry.getVatRate()));
        statement.setInt(6, carId.get());
        return statement;
      }, generatedKeyHolder);
    } else {
      jdbcTemplate.update(con -> {
        PreparedStatement statement = con.prepareStatement(emptyCarSql, new String[] {"id"});
        statement.setString(1, entry.getDescription());
        statement.setInt(2, entry.getQuantity());
        statement.setBigDecimal(3, entry.getNetPrice());
        statement.setBigDecimal(4, entry.getVatValue());
        statement.setInt(5, vatToId.get(entry.getVatRate()));
        return statement;
      }, generatedKeyHolder);
    }
    final int entryId = Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
    saveIdKeysToConnectedTable(invoiceId, entryId);
  }

  private Optional<Integer> saveCar(InvoiceEntry entry) {
    GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
    Optional<Car> car = Optional.ofNullable(entry.getCarRelatedExpenses());
    String sqlCar = """
        INSERT INTO public.car
        (registration_number, is_used_privately)
        values (?, ?);""";

    jdbcTemplate.update(connection -> {
      PreparedStatement statement = connection.prepareStatement(sqlCar, new String[] {"id"});
      statement.setString(1, car.get().getRegistrationNumber());
      statement.setBoolean(2, car.get().isUsedPrivately());
      return statement;
    }, generatedKeyHolder);
    return Optional.of(Objects.requireNonNull(generatedKeyHolder.getKey()).intValue());
  }

  private void saveIdKeysToConnectedTable(int invoiceId, int entryId) {
    String sqlConnectedTable = """
        INSERT INTO public.invoice_connected_to_entries
        (invoice_id, invoice_entry_id)
        values (?, ?);""";
    jdbcTemplate.update(con -> {
      PreparedStatement statement = con.prepareStatement(sqlConnectedTable);
      statement.setInt(1, invoiceId);
      statement.setInt(2, entryId);
      return statement;
    });
  }

  @Override
  public List<Invoice> getAll() {
    String sqlInvoice = """
        SELECT
        INV.id as invoice_id,
        INV.date,
        INV.number,
        C1.id as id_buyer,
        C1.tax_identification as tax_identification_buyer,
        C1.address as address_buyer,
        C1.name as name_buyer,
        C1.pension_insurance as pension_insurance_buyer,
        C1.health_insurance as health_insurance_buyer,
        C2.id as id_seller,
        C2.tax_identification as tax_identification_seller,
        C2.address as address_seller,
        C2.name as name_seller,
        C2.pension_insurance as pension_insurance_seller,
        C2.health_insurance as health_insurance_seller
        FROM invoices INV
        left join public.company C1 on INV.buyer = C1.id
        left join public.company C2 on INV.seller = C2.id;""";
    String sqlEntries = """
        SELECT
          IIE.invoice_id,
          IIE.invoice_entry_id,
          IE.description,
          IE.quantity,
          IE.net_price,
          IE.vat_value,
          V.name as vat_name,
          V.vat_rate,
          CR.id as id_car,
          CR.registration_number,
          CR.is_used_privately
          FROM public.invoice_connected_to_entries IIE
          left join public.invoice_entry IE on IIE.invoice_entry_id = IE.id
          left join public.vat V on IE.vat_rate = V.id
          left join public.car CR on IE.car_related_expenses = CR.id
          WHERE invoice_id = """;

    return jdbcTemplate.query(sqlInvoice, (resInvoice, rowNumber) -> {
      List<InvoiceEntry> invoiceEntries =
          jdbcTemplate.query(sqlEntries + resInvoice.getInt("invoice_id"), (resEntry, rowNum) -> InvoiceEntry.builder()
              .id(resEntry.getInt("invoice_entry_id"))
              .description(resEntry.getString("description"))
              .quantity(resEntry.getInt("quantity"))
              .netPrice(resEntry.getBigDecimal("net_price"))
              .vatValue(resEntry.getBigDecimal("vat_value"))
              .vatRate(Vat.valueOf(resEntry.getString("vat_name")))
              .carRelatedExpenses(Car.builder()
                  .id(resEntry.getInt("id_car"))
                  .registrationNumber(
                      Optional.ofNullable(resEntry.getString("registration_number"))
                          .isPresent() ? resEntry.getString("registration_number") : "No record")
                  .isUsedPrivately(resEntry.getBoolean("is_used_privately"))
                  .build())
              .build()
          );
      return Invoice.builder()
          .id(resInvoice.getInt("invoice_id"))
          .number(resInvoice.getString("number"))
          .date(resInvoice.getDate("date").toLocalDate())
          .buyer(Company.builder()
              .id(resInvoice.getInt("id_buyer"))
              .taxIdentification(resInvoice.getString("tax_identification_buyer"))
              .address(resInvoice.getString("address_buyer"))
              .name(resInvoice.getString("name_buyer"))
              .pensionInsurance(resInvoice.getBigDecimal("pension_insurance_buyer"))
              .healthInsurance(resInvoice.getBigDecimal("health_insurance_buyer"))
              .build()
          )
          .seller(Company.builder()
              .id(resInvoice.getInt("id_seller"))
              .taxIdentification(resInvoice.getString("tax_identification_seller"))
              .address(resInvoice.getString("address_seller"))
              .name(resInvoice.getString("name_seller"))
              .pensionInsurance(resInvoice.getBigDecimal("pension_insurance_seller"))
              .healthInsurance(resInvoice.getBigDecimal("health_insurance_seller"))
              .build()
          )
          .entries(invoiceEntries)
          .build();
    });
  }

  @Override
  public Optional<Invoice> getById(int id) {
    String sqlCheck = """
        SELECT * FROM public.invoices
        WHERE id = """ + id;
    String sqlInvoice = """
        SELECT
        INV.id as invoice_id,
        INV.date,
        INV.number,
        C1.id as id_buyer,
        C1.tax_identification as tax_identification_buyer,
        C1.address as address_buyer,
        C1.name as name_buyer,
        C1.pension_insurance as pension_insurance_buyer,
        C1.health_insurance as health_insurance_buyer,
        C2.id as id_seller,
        C2.tax_identification as tax_identification_seller,
        C2.address as address_seller,
        C2.name as name_seller,
        C2.pension_insurance as pension_insurance_seller,
        C2.health_insurance as health_insurance_seller
        FROM invoices INV
        left join public.company C1 on INV.buyer = C1.id
        left join public.company C2 on INV.seller = C2.id
        WHERE INV.id = """ + id;
    String sqlEntry = """
        SELECT
        IIE.invoice_id,
        IIE.invoice_entry_id,
        IE.description,
        IE.quantity,
        IE.net_price,
        IE.vat_value,
        V.name as vat_name,
        V.vat_rate,
        CR.id as id_car,
        CR.registration_number,
        CR.is_used_privately
        FROM public.invoice_connected_to_entries IIE
        left join public.invoice_entry IE on IIE.invoice_entry_id = IE.id
        left join public.vat V on IE.vat_rate = V.id
        left join public.car CR on IE.car_related_expenses = CR.id
        WHERE invoice_id = """ + id;

    Optional<List<Invoice>> searchedInvoice = Optional.of(
        jdbcTemplate.query(sqlCheck, (rs, rowNum) -> Invoice.builder()
            .id(rs.getInt("id"))
            .build())
    );
    return searchedInvoice.get().isEmpty() ? Optional.empty() : Optional.ofNullable(
        jdbcTemplate.queryForObject(sqlInvoice, (resInvoice, rowNum) -> {
          List<InvoiceEntry> invoiceEntries = jdbcTemplate.query(sqlEntry, (resEntry, rowNumber) ->
              InvoiceEntry.builder()
                  .id(resEntry.getInt("invoice_entry_id"))
                  .description(resEntry.getString("description"))
                  .quantity(resEntry.getInt("quantity"))
                  .netPrice(resEntry.getBigDecimal("net_price"))
                  .vatValue(resEntry.getBigDecimal("vat_value"))
                  .vatRate(Vat.valueOf(resEntry.getString("vat_name")))
                  .carRelatedExpenses(Car.builder()
                      .id(resEntry.getInt("id_car"))
                      .registrationNumber(
                          Optional.ofNullable(resEntry.getString("registration_number"))
                              .isPresent() ? resEntry.getString("registration_number") : "No record")
                      .isUsedPrivately(resEntry.getBoolean("is_used_privately"))
                      .build())
                  .build()
          );
          return Invoice.builder()
              .id(resInvoice.getInt("invoice_id"))
              .date((resInvoice.getDate("date")).toLocalDate())
              .number(resInvoice.getString("number"))
              .buyer(Company.builder()
                  .id(resInvoice.getInt("id_buyer"))
                  .taxIdentification(resInvoice.getString("tax_identification_buyer"))
                  .address(resInvoice.getString("address_buyer"))
                  .name(resInvoice.getString("name_buyer"))
                  .pensionInsurance(resInvoice.getBigDecimal("pension_insurance_buyer"))
                  .healthInsurance(resInvoice.getBigDecimal("health_insurance_buyer"))
                  .build())
              .seller(Company.builder()
                  .id(resInvoice.getInt("id_seller"))
                  .taxIdentification(resInvoice.getString("tax_identification_seller"))
                  .address(resInvoice.getString("address_seller"))
                  .name(resInvoice.getString("name_seller"))
                  .pensionInsurance(resInvoice.getBigDecimal("pension_insurance_seller"))
                  .healthInsurance(resInvoice.getBigDecimal("health_insurance_seller"))
                  .build())
              .entries(invoiceEntries)
              .build();
        }));
  }

  @Override
  public void delete(int id) {
    String sqlDeleteCon = """
        DELETE FROM public.invoice_connected_to_entries
        WHERE invoice_id = """ + id;
    String sqlDeleteInv = """
        DELETE FROM public.invoices
        WHERE id = """ + id;

    if (getById(id).isPresent()) {
      Invoice removedInvoice = getById(id).get();

      final List<Integer> companiesId =
          List.of(removedInvoice.getBuyer().getId(), removedInvoice.getSeller().getId());

      final List<Integer> entriesId =
          removedInvoice.getEntries().stream()
              .map(InvoiceEntry::getId)
              .collect(Collectors.toList());

      final Optional<List<Integer>> carsId =
          Optional.of(removedInvoice.getEntries().stream()
              .map(InvoiceEntry::getCarRelatedExpenses)
              .map(Car::getId).collect(Collectors.toList()));

      jdbcTemplate.update(sqlDeleteCon);
      jdbcTemplate.update(sqlDeleteInv);
      removeOrphanedEntries(entriesId);
      removeOrphanedCompanies(companiesId);
      removeOrphanedCars(carsId);
    }
  }

  private void removeOrphanedCars(Optional<List<Integer>> carsId) {
    String sqlStatement = """
        DELETE FROM public.car
        WHERE id = """;
    carsId.ifPresent(cars -> cars.stream().forEach(car -> jdbcTemplate.update(sqlStatement + car)));
  }

  private void removeOrphanedCompanies(List<Integer> companiesId) {
    String sqlStatement = """
        DELETE FROM public.company
        WHERE id = """;
    companiesId.stream().forEach(company -> jdbcTemplate.update(sqlStatement + company));
  }

  private void removeOrphanedEntries(List<Integer> entriesId) {
    String sqlStatement = """
        DELETE FROM public.invoice_entry
        WHERE id = """;
    entriesId.stream().forEach(entry -> jdbcTemplate.update(sqlStatement + entry));
  }

  @Override
  public void update(int id, Invoice updateInvoice) {
    String sqlInvoiceUpdate = """
        UPDATE public.invoices SET
        number = ?, date = ?
        WHERE id = """;
    String sqlCompanyUpdate = """
        UPDATE public.company SET
        tax_identification = ?, address = ?, name = ?,
        pension_insurance = ?, health_insurance = ?
        WHERE id = """;

    if (getById(id).isPresent()) {
      final Invoice originalInvoice = getById(id).get();
      final List<Integer> originalCompaniesId =
          List.of(originalInvoice.getBuyer().getId(),
              originalInvoice.getSeller().getId());

      updateInvoice.setId(id);
      int buyerId = originalCompaniesId.get(0);
      int sellerId = originalCompaniesId.get(1);

      jdbcTemplate.update(sqlInvoiceUpdate + id, psInvoice -> {
        psInvoice.setString(1, updateInvoice.getNumber());
        psInvoice.setDate(2, Date.valueOf(updateInvoice.getDate()));
        jdbcTemplate.update(sqlCompanyUpdate + buyerId, psBuyer -> {
          psBuyer.setString(1, updateInvoice.getBuyer().getTaxIdentification());
          psBuyer.setString(2, updateInvoice.getBuyer().getAddress());
          psBuyer.setString(3, updateInvoice.getBuyer().getName());
          psBuyer.setBigDecimal(4, updateInvoice.getBuyer().getPensionInsurance());
          psBuyer.setBigDecimal(5, updateInvoice.getBuyer().getHealthInsurance());
          jdbcTemplate.update(sqlCompanyUpdate + sellerId, psSeller -> {
            psSeller.setString(1, updateInvoice.getSeller().getTaxIdentification());
            psSeller.setString(2, updateInvoice.getSeller().getAddress());
            psSeller.setString(3, updateInvoice.getSeller().getName());
            psSeller.setBigDecimal(4, updateInvoice.getSeller().getPensionInsurance());
            psSeller.setBigDecimal(5, updateInvoice.getSeller().getHealthInsurance());
          });
        });
      });
      updateEntries(id, updateInvoice.getEntries(), originalInvoice.getEntries());
    }
  }

  private void updateEntries(int invoiceId, List<InvoiceEntry> updatedEntries, List<InvoiceEntry> originalEntries) {
    final String sqlDeleteConnection = """
        DELETE FROM public.invoice_connected_to_entries
        WHERE invoice_id = """;
    final String sqlInvoices = """
        UPDATE public.invoices
        SET invoice_entry = ?
        WHERE id = """;

    List<Integer> originalEntriesId = originalEntries.stream()
        .map(InvoiceEntry::getId)
        .collect(Collectors.toList());

    Optional<List<Integer>> originalCarsId = Optional.of(originalEntries.stream()
        .map(InvoiceEntry::getCarRelatedExpenses)
        .map(Car::getId)
        .collect(Collectors.toList()));

    jdbcTemplate.update(sqlDeleteConnection + invoiceId);
    removeOrphanedEntries(originalEntriesId);
    removeOrphanedCars(originalCarsId);

    updatedEntries.stream()
        .forEach(entry -> saveEntry(invoiceId, entry));
    jdbcTemplate.update(sqlInvoices + invoiceId, invoiceId);
  }
}
