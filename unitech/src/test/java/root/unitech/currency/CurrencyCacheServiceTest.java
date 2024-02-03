package root.unitech.currency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class CurrencyCacheServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache exchangeRatesCache;

    @InjectMocks
    private CurrencyCacheService currencyCacheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cacheManager.getCache("exchangeRates")).thenReturn(exchangeRatesCache);
    }

    @Test
    @DisplayName("USER COULD fetch currency rate successfully")
    void fetchSelectedCurrencyRate_Success() {
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        String expectedRate = "1.2";

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(ResponseEntity.ok(expectedRate));
        ResponseEntity<String> response = currencyCacheService.fetchSelectedCurrencyRate(fromCurrency, toCurrency);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("USD/EUR = 1.2", response.getBody());

    }


    @Test
    @DisplayName("USER requested currency rate is already present in cache")
    void fetchSelectedCurrencyRate_CacheHit() {
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        String cachedRate = "1.2";

        when(exchangeRatesCache.get(anyString())).thenReturn(() -> cachedRate);

        ResponseEntity<String> response = currencyCacheService.fetchSelectedCurrencyRate(fromCurrency, toCurrency);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("USD/EUR = 1.2", response.getBody());

    }

    @Test
    @DisplayName("Fetch Currency Rate - Invalid Currencies")
    void fetchSelectedCurrencyRate_InvalidCurrencies() {
        String fromCurrency = "INVALID";
        String toCurrency = "EUR";

        ResponseEntity<String> response = currencyCacheService.fetchSelectedCurrencyRate(fromCurrency, toCurrency);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Entered currencies do not exist in our system!", response.getBody());
    }
}