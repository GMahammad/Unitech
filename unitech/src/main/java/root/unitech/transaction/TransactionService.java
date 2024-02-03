package root.unitech.transaction;

import root.unitech.account.Account;
import root.unitech.account.AccountCurrency;
import root.unitech.account.AccountRepository;
import root.unitech.currency.CurrencyCacheService;
import root.unitech.requests.TransactionRequest;
import root.unitech.utils.ExtractExchangeRate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CurrencyCacheService currencyCacheService;

    //ADMIN -> can get all transactions in the database;
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }


    //Main Create transaction service;
    public ResponseEntity createTransaction(TransactionRequest transactionRequest) {
        try {
            String authenticatedUserPin = getAuthenticatedUserPin();

            Account senderAccount = findOrThrowAccount(transactionRequest.getSenderAccount()) ;
            Account receiverAccount = findOrThrowAccount(transactionRequest.getReceiverAccount());

            BigDecimal currencyRate = checkCurrencyRateForTransaction(transactionRequest,senderAccount,receiverAccount);

            validateTransactionConditions(senderAccount, receiverAccount, transactionRequest);
            createAndSaveTransaction(transactionRequest, senderAccount, receiverAccount,currencyRate);

            return ResponseEntity.ok("Transfer completed successfully");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("An error occurred: " + e.getMessage());
        }
    }


    /*Helper functions START

    /*Check currency rate if accounts of sender and receiver are same then do not apply currency rate on transaction balance
        else change currency rate */
    private BigDecimal checkCurrencyRateForTransaction(TransactionRequest transactionRequest, Account senderAccount, Account receiverAccount){
        AccountCurrency senderCurrency =  senderAccount.getAccountCurrency();
        AccountCurrency receiverCurrency =  receiverAccount.getAccountCurrency();
        BigDecimal currencyRate = BigDecimal.ONE;

        if(!senderCurrency.equals(receiverCurrency)){
            String currencyRateStr = currencyCacheService.fetchSelectedCurrencyRate(senderCurrency.name(),receiverCurrency.name()).getBody();
            currencyRate = ExtractExchangeRate.extractExchangeRate(currencyRateStr);
        }
        return currencyRate;
    }

    //Check  user is successfully authentication or not and get userPin(userName) from authentication;
    private String getAuthenticatedUserPin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((UserDetails) authentication.getPrincipal()).getUsername();
    }

    //Try to check sender and receiver accounts or catch Exception
    private Account findOrThrowAccount(String accountNumber) throws Exception {
        try {
           return accountRepository.findByAccountNumber(accountNumber).get();
        }catch (Exception e) {
            throw new Exception("Account number is not correct or it does not exist in our system");
        }
    }

    //Validate Transaction conditions before creating and saving it;
    private void validateTransactionConditions(Account senderAccount, Account receiverAccount, TransactionRequest transactionRequest) throws Exception {
        if (senderAccount.equals(receiverAccount)) {
            throw new Exception("Sender and receiver accounts are the same!");
        }

        if (!receiverAccount.isActive()) {
            throw new Exception("You tried to make a transfer to a deactivated account!");
        }

        if (senderAccount.getBalance().compareTo(transactionRequest.getTransactionBalance()) < 0) {
            throw new Exception("You do not have enough balance for making a transfer!");
        }

    }

    //Initially create transaction itself.After that set balance, received and sent transactions of both accounts. Then save accounts and transaction;
    private void createAndSaveTransaction(TransactionRequest transactionRequest, Account senderAccount, Account receiverAccount,BigDecimal currencyRate) {
        Transaction transaction = Transaction.builder()
                .createdAt(LocalDateTime.now())
                .transactionBalance(transactionRequest.getTransactionBalance())
                .sender(senderAccount)
                .receiver(receiverAccount)
                .build();

        BigDecimal convertedValueOfTransaction = transactionRequest.getTransactionBalance().multiply(currencyRate);

        senderAccount.setBalance(senderAccount.getBalance().subtract(transactionRequest.getTransactionBalance()));
        receiverAccount.setBalance(receiverAccount.getBalance().add(convertedValueOfTransaction));
        receiverAccount.getReceivedTransactions().add(transaction);
        senderAccount.getSentTransactions().add(transaction);

        accountRepository.saveAll(List.of(receiverAccount, senderAccount));
        transactionRepository.save(transaction);
    }

     //Helper functions END
}
