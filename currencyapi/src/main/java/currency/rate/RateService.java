package currency.rate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class RateService {

    public ResponseEntity<BigDecimal> getCurrencyRate(String fromCurrency,String toCurrency){
        BigDecimal exchangeRate = generateRandomRate(fromCurrency,toCurrency);
        if (exchangeRate != null) {
            return ResponseEntity.ok(exchangeRate);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    private BigDecimal generateRandomRate(String fromCurrency, String toCurrency) {
        System.out.println("Making a request to the third-party service for exchange rate between " + fromCurrency + " and " + toCurrency + "...");
        double randomRate = 1 + Math.random() * 9;
        BigDecimal exchangeRate = BigDecimal.valueOf(randomRate).setScale(2, RoundingMode.HALF_UP);
        return exchangeRate;
    }


}
