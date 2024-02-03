package root.unitech.utils;

import root.unitech.account.Account;
import root.unitech.account.AccountCurrency;
import root.unitech.account.AccountRepository;
import root.unitech.auth.AuthRepository;
import root.unitech.auth.Role;
import root.unitech.auth.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DummyDataInitializer {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    @PostConstruct
    public void insertAdmin() {
        String adminPin = "admin";
        String adminPassword = "password";

        String userPin = "user";
        String userPassword = "Mahammad";

        // User does not exist, create and save the admin user
        if (!authRepository.findByPin(adminPin).isPresent()) {
            User admin = User.builder().pin(adminPin).password(passwordEncoder.encode(adminPassword)).role(Role.ADMIN).build();

            Account account = Account.builder()
                    .accountNumber("TestAdminAccount")
                    .accountCurrency(AccountCurrency.USD)
                    .isActive(true)
                    .balance(BigDecimal.valueOf(1000))
                    .user(admin)
                    .build();

            authRepository.save(admin);
            accountRepository.save(account);
        }
        // User does not exist, create and save the user
        if (!authRepository.findByPin(userPin).isPresent()) {
            User user = User.builder().pin(userPin).password(passwordEncoder.encode(userPassword)).role(Role.USER).build();
            Account account = Account.builder()
                    .accountNumber("TestUserAccount")
                    .accountCurrency(AccountCurrency.AZN)
                    .isActive(true)
                    .balance(BigDecimal.valueOf(2000))
                    .user(user)
                    .build();
            authRepository.save(user);
            accountRepository.save(account);

        }
    }
}
