package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Company {

  @ApiModelProperty(value = "Tax identyfication number", required = true, example = "555-444-33-22")
  private String taxIdentification;
  @ApiModelProperty(value = "Company address", required = true, example = "30-010 Krakow, Wroclawska 7")
  private String address;
  @ApiModelProperty(value = "Company name", required = true, example = "Best Code S.A.")
  private String name;
  @Builder.Default
  @ApiModelProperty(value = "Pension insurance expenses", required = true, example = "1873.43")
  private BigDecimal pensionInsurance = BigDecimal.ZERO;
  @Builder.Default
  @ApiModelProperty(value = "Health insurance expenses", required = true, example = "1420.88")
  private BigDecimal healthInsurance = BigDecimal.ZERO;

  public Company() {
  }

  public Company(String taxIdentification, String address, String name,
                 BigDecimal pensionInsurance, BigDecimal healthInsurance) {
    this.taxIdentification = taxIdentification;
    this.address = address;
    this.name = name;
    this.pensionInsurance = pensionInsurance;
    this.healthInsurance = healthInsurance;
  }
}
