package pl.futurecollars.invoicing.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Vat {

  VAT_23(23.00),
  VAT_19(19.00),
  VAT_9(9.00),
  VAT_8(8.00),
  VAT_7_75(7.75),
  VAT_5(5.00),
  VAT_0(0.00);

  private final double rate;

  Vat(double rate) {
    this.rate = rate / 100;
  }
}
