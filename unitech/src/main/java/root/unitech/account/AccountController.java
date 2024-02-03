package root.unitech.account;

import root.unitech.requests.CreateAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/account/")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/myaccounts")
    public ResponseEntity<List<Account>> getActiveAccount(){
        return accountService.getActiveAccountOfUser();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/createaccount")
    public ResponseEntity<?> addBankAccount(@RequestBody CreateAccountRequest createAccountRequest) throws Exception {
        return accountService.createAccount(createAccountRequest);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/changeaccountstatus")
    public ResponseEntity<?> changeStatus(@RequestParam Long accountId) throws Exception {
        return accountService.changeStatusAccount(accountId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/allaccounts")
    public List<Account> getAllAccounts() throws Exception {
        return accountService.getAllAccounts();
    }

}
