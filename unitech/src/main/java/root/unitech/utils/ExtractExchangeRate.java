package root.unitech.utils;

import java.math.BigDecimal;

public class ExtractExchangeRate {

    //Helper function for extracting exchange rate from third party currencyAPI response.
    public static BigDecimal extractExchangeRate(String input) {
        String numericPart = input.replaceAll("[^\\d.]", "");
        return new BigDecimal(numericPart);
    }
}
