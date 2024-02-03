package currency.rate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tpapi/v1")
public class RateController {

    private final RateService rateService;

    @GetMapping("/rate")
    public ResponseEntity<BigDecimal> getCurrencyRate(@RequestParam String fromCurrency, @RequestParam String toCurrency)
    {
        return rateService.getCurrencyRate(fromCurrency,toCurrency);
    }
}
