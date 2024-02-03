package root.unitech.currency;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/currency")
@RequiredArgsConstructor
public class CurrencyController {
    private final CurrencyCacheService currencyCacheService;

    @GetMapping("/rate")
    public ResponseEntity<String> getCurrencyRate(@RequestParam String from,@RequestParam String to) {
        return currencyCacheService.fetchSelectedCurrencyRate(from,to);
    }
}
