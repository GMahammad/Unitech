package root.unitech.account;

import root.unitech.auth.AuthRepository;
import root.unitech.utils.RandomAccountGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@DisplayName("Account Repository test cases:")
public
class AccountRepositoryTest {

    @Mock
    private AuthRepository authRepository;

    @Autowired
    private AccountRepository accountRepository;

    @AfterEach
    void tearDown() {
        authRepository.deleteAll();
    }


    @DisplayName("Repo COULD find account by accountId")
    @Test
    void findById() {
        Account account = Account.builder()
                .accountNumber(RandomAccountGenerator.randomAccountNumberGenerator())
                .isActive(true)
                .build();
        accountRepository.save(account);

        Account expectedAccount = accountRepository.findById(account.getAccountId()).orElseThrow(null);
        assertThat(expectedAccount).isNotNull();
        assertThat(expectedAccount.getAccountId()).isEqualTo(account.getAccountId());
    }

    @DisplayName("Repo COULD NOT find account by accountId")
    @Test
    void findByIdNExist() {
       Long wrongAccountId = 23L;
       Account wrongAccount = accountRepository.findById(wrongAccountId).orElse(null);
       assertThat(wrongAccount).isNull();
    }

    @Test
    void findByAccountNumber() {
        Account account = Account.builder()
                .balance(BigDecimal.valueOf(543))
                .accountNumber(RandomAccountGenerator.randomAccountNumberGenerator())
                .isActive(true)
                .build();

        accountRepository.save(account);
        Account expectedAccount = accountRepository.findByAccountNumber(account.getAccountNumber()).orElseThrow(null);

        assertThat(expectedAccount).isNotNull();
        assertThat(expectedAccount.getAccountNumber()).isEqualTo(account.getAccountNumber());
    }

    @Test
    void findByNotAccountNumber() {
        String auxAccountNumber = RandomAccountGenerator.randomAccountNumberGenerator();

        Account wrongAccount = accountRepository.findByAccountNumber(auxAccountNumber).orElse(null);
        assertThat(wrongAccount).isNull();
    }
}