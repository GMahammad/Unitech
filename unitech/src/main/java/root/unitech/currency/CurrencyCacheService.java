package root.unitech.currency;

import root.unitech.account.AccountCurrency;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CurrencyCacheService {

    private final RestTemplate restTemplate;
    private final CacheManager cacheManager;

    //This function evict/clear the cache after 1 minute
    @Scheduled(fixedRate = 60000)
    @CacheEvict(cacheNames = "exchangeRates", allEntries = true)
    public void evictCache() {
        System.out.println("Cache cleared at " + LocalDateTime.now().toLocalTime());
    }

    //This function helps us for adding new value to cache
    @CachePut(cacheNames = "exchangeRates")
    public void cacheCurrencyRate(String fromCurrency, String toCurrency, String rate) {
        cacheManager.getCache("exchangeRates").put(fromCurrency + "/" + toCurrency, rate);
    }

    //This function fetch selected currency and send to cache but also store vice versa currency too.
    // For instance USD/AZN = 0.58 -> AZN/USD = 1.7 and both stored in cache for decreasing cost and increasing efficiency
    public ResponseEntity<String> fetchSelectedCurrencyRate(String fromCurrency, String toCurrency) {
        try {

            AccountCurrency.valueOf(fromCurrency.toUpperCase());
            AccountCurrency.valueOf(toCurrency.toUpperCase());

            //If both currency are same we just return 1
            if (fromCurrency.equals(toCurrency)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(fromCurrency + "/" + toCurrency + " = " + BigDecimal.ONE);
            }
            //WITHIN ***1 MINUTE***
            //If USD/AZN stored in cache. We return immediately AZN/USD without calling API
            if (cacheManager.getCache("exchangeRates").get(fromCurrency + "/" + toCurrency) != null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(fromCurrency + "/" + toCurrency + " = " + cacheManager.getCache("exchangeRates").get(fromCurrency + "/" + toCurrency).get());
            }

            String url = String.format("http://localhost:8081/tpapi/v1/rate?fromCurrency=%s&toCurrency=%s",
                    fromCurrency, toCurrency);

            ResponseEntity checkedRest = getCheckedRest(url);
            String currencyRate = checkedRest.getBody().toString();
            String reverseRate = BigDecimal.ONE.divide(new BigDecimal(currencyRate), 2, RoundingMode.HALF_UP).toString();

            cacheCurrencyRate(fromCurrency, toCurrency, currencyRate);
            cacheCurrencyRate(toCurrency, fromCurrency, reverseRate);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(fromCurrency + "/" + toCurrency + " = " + currencyRate);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Entered currencies do not exist in our system!");
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    private ResponseEntity getCheckedRest(String url) throws Exception {
        try {
            ResponseEntity restEntity = restTemplate.getForEntity(url,String.class);
            return restEntity;
        }catch (Exception e){
            throw new Exception("Currently currencyAPI does not work.Please try again later.\nReason:"+e.getMessage());
        }
    }


}
