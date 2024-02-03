package root.unitech.account;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface AccountRepository extends JpaRepository<Account,Long> {

    Optional<Account> findById(Long accoundId);
    Optional<Account> findByAccountNumber(String accountNumber);
}
