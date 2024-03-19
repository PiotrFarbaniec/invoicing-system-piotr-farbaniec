package pl.futurecollars.invoicing.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Vat {

  VAT_23(23),
  VAT_8(8),
  VAT_5(5),
  VAT_0(0);

  private final int rate;

  Vat(int rate) {
    this.rate = rate;
  }
}
