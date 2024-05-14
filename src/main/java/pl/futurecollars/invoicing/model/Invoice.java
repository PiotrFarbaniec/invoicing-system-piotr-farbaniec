package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

  @ApiModelProperty(value = "ID - automatically generated by the application", required = true, example = "1")
  private int id;

  @ApiModelProperty(value = "Unique invoice number", required = true, example = "GF308455600")
  private String number;

  @ApiModelProperty(value = "Date of issue of the invoice", required = true, example = "2023-10-23")
  private LocalDate date;

  @ApiModelProperty(value = "Company purchasing the product/service", required = true)
  private Company buyer;

  @ApiModelProperty(value = "Company selling the product/service", required = true)
  private Company seller;

  @ApiModelProperty(value = "All invoiced products/services", required = true)
  private List<InvoiceEntry> entries;
}
