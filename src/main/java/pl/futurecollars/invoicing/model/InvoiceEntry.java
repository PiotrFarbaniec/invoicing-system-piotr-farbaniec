package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceEntry {

  @ApiModelProperty(value = "Product/service description", required = true, example = "management software")
  private String description;
  @ApiModelProperty(value = "Quantity of product/service", required = true, example = "3")
  private int quantity;
  @ApiModelProperty(value = "Net cost of the product/service", required = true, example = "10000")
  private BigDecimal netPrice;
  @ApiModelProperty(value = "Value of tax costs", required = true, example = "2314.54")
  private BigDecimal vatValue;
  @ApiModelProperty(value = "Applicable VAT rate", required = true)
  private Vat vatRate;
  @ApiModelProperty(value = "Costs associated with a car while purchase/sell products or services")
  private Car carRelatedExpenses;

  public InvoiceEntry() {
  }

  public InvoiceEntry(String description, int quantity, BigDecimal netPrice, BigDecimal vatValue, Vat vatRate, Car expensionRelatedToCar) {
    this.description = description;
    this.quantity = quantity;
    this.netPrice = netPrice;
    this.vatValue = vatValue;
    this.vatRate = vatRate;
    this.carRelatedExpenses = expensionRelatedToCar;
  }
}
