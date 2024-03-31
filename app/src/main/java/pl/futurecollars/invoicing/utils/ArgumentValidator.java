package pl.futurecollars.invoicing.utils;

public class ArgumentValidator {

  public static <T> void ensureArgumentNotNull(T arg, String argName) {
    if (arg == null) {
      throw new IllegalArgumentException(String.format("%s cant't be null", argName));
    }
  }
}
