package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Company {

  @ApiModelProperty(value = "Tax identyfication number", required = true, example = "555-444-33-22")
  private String taxIdentifications;
  @ApiModelProperty(value = "Company address", required = true, example = "30-010 Krakow, Wroclawska 7")
  private String address;
  @ApiModelProperty(value = "Company name", required = true, example = "Best Code S.A.")
  private String name;

  public Company() {
  }

  public Company(String taxIdentifications, String address, String name) {
    this.taxIdentifications = taxIdentifications;
    this.address = address;
    this.name = name;
  }
}
