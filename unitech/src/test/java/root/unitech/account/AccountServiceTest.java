package root.unitech.account;

import root.unitech.auth.AuthRepository;
import root.unitech.auth.User;
import root.unitech.requests.CreateAccountRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Bank Account Service test cases: ")
class AccountServiceTest {
    @Mock
    private AuthRepository authRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;


    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("User COULD create account")
    @Test
    void createAccount() throws Exception {
        CreateAccountRequest createAccountRequest = new CreateAccountRequest("userPin", BigDecimal.valueOf(100), "USD");
        User foundUser = User.builder().pin("userPin").build();

        when(authRepository.findByPin("userPin")).thenReturn(Optional.of(foundUser));

        ResponseEntity responseEntity = accountService.createAccount(createAccountRequest);

        assertEquals(ResponseEntity.ok("Account created successfully for UserPin: userPin"), responseEntity);

    }

    @DisplayName("User COULD NOT create account due to invalid userPin")
    @Test
    void createAccountUserPinNExist() throws Exception {
        String nonExistentUserPin = "9999";
        CreateAccountRequest createAccountRequest = CreateAccountRequest
                .builder()
                .userPin(nonExistentUserPin)
                .balance(BigDecimal.valueOf(100))
                .build();
        when(authRepository.findByPin(nonExistentUserPin)).thenReturn(Optional.empty());

        ResponseEntity<?> response = accountService.createAccount(createAccountRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @DisplayName("User COULD get active accounts")
    @Test
    void getActiveAccountOfUser() throws Exception {
        String userPin = "1234";
        User foundUser = new User();

        when(authRepository.findByPin(userPin)).thenReturn(Optional.of(foundUser));
        when(userDetails.getUsername()).thenReturn(userPin);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResponseEntity<List<Account>> response = accountService.getActiveAccountOfUser();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(authRepository).findByPin(userPin);
    }

    @DisplayName("Admin COULD change the account status")
    @Test
    void changeStatusAccount() throws Exception {
        Long accountId = 123L;
        Account foundAccount = new Account();
        foundAccount.setAccountId(accountId);
        foundAccount.setActive(true);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(foundAccount));
        ResponseEntity<?> response = accountService.changeStatusAccount(accountId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(!foundAccount.isActive());
    }

    @DisplayName("Admin COULD get all accounts")
    @Test
    void getAllAccounts() throws Exception {
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountService accountService = new AccountService(accountRepository, null);

        when(accountRepository.findAll()).thenReturn(Arrays.asList(new Account(), new Account()));
        List<Account> allAccounts = accountService.getAllAccounts();

        assertNotNull(allAccounts);
        assertEquals(2, allAccounts.size());
        verify(accountRepository).findAll();
    }
}