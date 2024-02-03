package root.unitech.transaction;

import root.unitech.account.Account;
import root.unitech.account.AccountCurrency;
import root.unitech.account.AccountRepository;
import root.unitech.currency.CurrencyCacheService;
import root.unitech.requests.TransactionRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class TransactionServiceTest {

    @Mock
    private CurrencyCacheService currencyCacheService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;


    @InjectMocks
    private TransactionService transactionService;

    private void setupSecurityContext(String username) {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Authentication authentication = Mockito.mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        UserDetails userDetails = new User(username, "password", Collections.emptyList());
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    @DisplayName("User COULD make a transfer")
    @Test
    void createTransaction_SuccessfulTransfer() {
        MockitoAnnotations.openMocks(this);

        setupSecurityContext("username");

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setSenderAccount("senderAccountNumber");
        transactionRequest.setReceiverAccount("receiverAccountNumber");
        transactionRequest.setTransactionBalance(BigDecimal.TEN);

        Account senderAccount = new Account();
        senderAccount.setAccountNumber("senderAccountNumber");
        senderAccount.setBalance(BigDecimal.valueOf(20));
        senderAccount.setActive(true);
        senderAccount.setAccountCurrency(AccountCurrency.USD);


        Account receiverAccount = new Account();
        receiverAccount.setAccountNumber("receiverAccountNumber");
        receiverAccount.setBalance(BigDecimal.valueOf(20));
        receiverAccount.setActive(true);
        receiverAccount.setAccountCurrency(AccountCurrency.USD);


        when(accountRepository.findByAccountNumber("senderAccountNumber")).thenReturn(java.util.Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber("receiverAccountNumber")).thenReturn(java.util.Optional.of(receiverAccount));
        when(currencyCacheService.fetchSelectedCurrencyRate(anyString(), anyString())).thenReturn(ResponseEntity.ok("1.2")); // Mocking currency rate
        ResponseEntity responseEntity = transactionService.createTransaction(transactionRequest);

        assertEquals(ResponseEntity.ok("Transfer completed successfully"), responseEntity);
    }

    @DisplayName("User COULD NOT make a transfer. Because receiver is deactive")
    @Test
    void createTransaction_TransferDeactiveAccount() {
        MockitoAnnotations.openMocks(this);

        setupSecurityContext("username");

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setSenderAccount("senderAccountNumber");
        transactionRequest.setReceiverAccount("receiverAccountNumber");
        transactionRequest.setTransactionBalance(BigDecimal.TEN);

        Account senderAccount = new Account();
        senderAccount.setAccountNumber("senderAccountNumber");
        senderAccount.setBalance(BigDecimal.valueOf(20));
        senderAccount.setActive(true);
        senderAccount.setAccountCurrency(AccountCurrency.USD);


        Account receiverAccount = new Account();
        receiverAccount.setAccountNumber("receiverAccountNumber");
        receiverAccount.setBalance(BigDecimal.valueOf(20));
        receiverAccount.setActive(false);
        receiverAccount.setAccountCurrency(AccountCurrency.USD);


        when(accountRepository.findByAccountNumber("senderAccountNumber")).thenReturn(java.util.Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber("receiverAccountNumber")).thenReturn(java.util.Optional.of(receiverAccount));
        when(currencyCacheService.fetchSelectedCurrencyRate(anyString(), anyString())).thenReturn(ResponseEntity.ok("1.2")); // Mocking currency rate
        ResponseEntity responseEntity = transactionService.createTransaction(transactionRequest);

        assertEquals(ResponseEntity.badRequest().body("An error occurred: You tried to make a transfer to a deactivated account!"), responseEntity);
    }

    @DisplayName("User COULD NOT make a transfer. Because sender balance is not enough")
    @Test
    void createTransaction_NotEnoughBalance() {
        MockitoAnnotations.openMocks(this);

        setupSecurityContext("username");

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setSenderAccount("senderAccountNumber");
        transactionRequest.setReceiverAccount("receiverAccountNumber");
        transactionRequest.setTransactionBalance(BigDecimal.TEN);

        Account senderAccount = new Account();
        senderAccount.setAccountNumber("senderAccountNumber");
        senderAccount.setBalance(BigDecimal.valueOf(9));
        senderAccount.setActive(true);
        senderAccount.setAccountCurrency(AccountCurrency.USD);


        Account receiverAccount = new Account();
        receiverAccount.setAccountNumber("receiverAccountNumber");
        receiverAccount.setBalance(BigDecimal.valueOf(20));
        receiverAccount.setActive(true);
        receiverAccount.setAccountCurrency(AccountCurrency.USD);


        when(accountRepository.findByAccountNumber("senderAccountNumber")).thenReturn(java.util.Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber("receiverAccountNumber")).thenReturn(java.util.Optional.of(receiverAccount));
        when(currencyCacheService.fetchSelectedCurrencyRate(anyString(), anyString())).thenReturn(ResponseEntity.ok("1.2")); // Mocking currency rate
        ResponseEntity responseEntity = transactionService.createTransaction(transactionRequest);

        assertEquals(ResponseEntity.badRequest().body("An error occurred: You do not have enough balance for making a transfer!"), responseEntity);
    }

    @DisplayName("User COULD NOT make a transfer. Because receiver account is not exists")
    @Test
    void createTransaction_AccountNumberNonExist() {
        MockitoAnnotations.openMocks(this);

        setupSecurityContext("username");

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setSenderAccount("senderAccountNumber");
        transactionRequest.setReceiverAccount("receiverAccountNumber");
        transactionRequest.setTransactionBalance(BigDecimal.TEN);

        Account senderAccount = new Account();
        senderAccount.setAccountNumber("senderAccountNumber");
        senderAccount.setBalance(BigDecimal.valueOf(9));
        senderAccount.setActive(true);
        senderAccount.setAccountCurrency(AccountCurrency.USD);

        when(accountRepository.findByAccountNumber("senderAccountNumber")).thenReturn(java.util.Optional.of(senderAccount));
        when(currencyCacheService.fetchSelectedCurrencyRate(anyString(), anyString())).thenReturn(ResponseEntity.ok("1.2"));
        ResponseEntity responseEntity = transactionService.createTransaction(transactionRequest);

        assertEquals(ResponseEntity.badRequest().body("An error occurred: Account number is not correct or it does not exist in our system"), responseEntity);
    }

    @DisplayName("User COULD NOT make a transfer. Because sender and receiver accounts are the same!")
    @Test
    void createTransaction_AccountNumbersSame() {
        MockitoAnnotations.openMocks(this);

        setupSecurityContext("username");

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setSenderAccount("senderAccountNumber");
        transactionRequest.setReceiverAccount("senderAccountNumber");
        transactionRequest.setTransactionBalance(BigDecimal.TEN);

        Account senderAccount = new Account();
        senderAccount.setAccountNumber("senderAccountNumber");
        senderAccount.setBalance(BigDecimal.valueOf(9));
        senderAccount.setActive(true);
        senderAccount.setAccountCurrency(AccountCurrency.USD);

        when(accountRepository.findByAccountNumber("senderAccountNumber")).thenReturn(java.util.Optional.of(senderAccount));
        when(currencyCacheService.fetchSelectedCurrencyRate(anyString(), anyString())).thenReturn(ResponseEntity.ok("1.2")); // Mocking currency rate
        ResponseEntity responseEntity = transactionService.createTransaction(transactionRequest);

        assertEquals(ResponseEntity.badRequest().body("An error occurred: Sender and receiver accounts are the same!"), responseEntity);
    }

}