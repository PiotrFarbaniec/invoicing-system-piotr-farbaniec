package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class InvoiceEntry {

  @ApiModelProperty(value = "Product/service description", required = true, example = "management software")
  private String description;
  @ApiModelProperty(value = "Quantity of product/service", required = true, example = "1")
  private int quantity;
  @ApiModelProperty(value = "Net cost of the product/service", required = true, example = "10 000")
  private BigDecimal price;
  @ApiModelProperty(value = "Value of tax costs", required = true, example = "2300")
  private BigDecimal vatValue;
  @ApiModelProperty(value = "Applicable VAT rate", required = true)
  private Vat vatRate;

  public InvoiceEntry() {
  }

  public InvoiceEntry(String description, int quantity, BigDecimal price, BigDecimal vatValue, Vat vatRate) {
    this.description = description;
    this.quantity = quantity;
    this.price = price;
    this.vatValue = vatValue;
    this.vatRate = vatRate;
  }
}
