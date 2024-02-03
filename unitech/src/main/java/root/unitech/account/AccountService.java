package root.unitech.account;

import root.unitech.auth.AuthRepository;
import root.unitech.auth.User;
import root.unitech.requests.CreateAccountRequest;
import root.unitech.utils.RandomAccountGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AuthRepository authRepository;


    public ResponseEntity createAccount(CreateAccountRequest createAccountRequest){
        try {
            User foundUser =getUser(createAccountRequest.getUserPin());

            enumChecker(createAccountRequest);
            accountBuilder(createAccountRequest, foundUser);

            return ResponseEntity.ok("Account created successfully for UserPin: " + foundUser.getPin());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }



    public ResponseEntity getActiveAccountOfUser()  {
        try {

            String userPin = authenticatedUserPinChecker();
            User foundUser = getUser(userPin);

            List<Account> activeAccounts = foundUser.getAccounts()
                    .stream().filter(account -> account.isActive()).toList();

            return ResponseEntity.ok(activeAccounts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public ResponseEntity changeStatusAccount(Long accountId) throws Exception {
        try {
            Account foundAccount = accountRepository.findById(accountId).orElseThrow(() -> new Exception("Account can not be found with given Account ID!"));
            foundAccount.setActive(!foundAccount.isActive());

            return ResponseEntity.ok("Status of account was changed successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    //__Helper Methods__Start__
    private User getUser(String userPin) throws Exception {
        User foundUser = authRepository.findByPin(userPin).orElseThrow(() -> new Exception("User can not be found with given User Pin!"));
        return foundUser;
    }

    private static String authenticatedUserPinChecker() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserPin = ((UserDetails) authentication.getPrincipal()).getUsername();
        return authenticatedUserPin;
    }

    private static void enumChecker(CreateAccountRequest createAccountRequest) throws Exception {
        boolean isValidEnum = Arrays.stream(AccountCurrency.values())
                .anyMatch(enumConstant -> enumConstant.name()
                        .equalsIgnoreCase(createAccountRequest.getAccountCurrency().toString()));

        if (!isValidEnum) {
            throw new Exception("Enter correct currency");
        }
    }

    private void accountBuilder(CreateAccountRequest createAccountRequest, User foundUser) {
        Account account = Account.builder()
                .accountNumber(RandomAccountGenerator.randomAccountNumberGenerator())
                .isActive(true)
                .balance(createAccountRequest.getBalance() == null ? BigDecimal.ZERO : createAccountRequest.getBalance())
                .accountCurrency(AccountCurrency.valueOf(createAccountRequest.getAccountCurrency()))
                .user(foundUser)
                .build();

        accountRepository.save(account);
    }
    //__Helper Methods__End__

}


