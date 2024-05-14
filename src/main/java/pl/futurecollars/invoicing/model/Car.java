package pl.futurecollars.invoicing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Car {

  @JsonIgnore
  @ApiModelProperty(value = "ID - automatically generated by the application", required = true, example = "1")
  private int id;

  @ApiModelProperty(value = "Car registration number", required = true, example = "KR 45777")
  private String registrationNumber;

  @ApiModelProperty(value = "Information is the vehicle also used privately", required = true, example = "true/false")
  private boolean isUsedPrivately;
}
