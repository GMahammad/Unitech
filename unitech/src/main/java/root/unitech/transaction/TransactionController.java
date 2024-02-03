package root.unitech.transaction;

import root.unitech.requests.TransactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    //ADMIN -> can select all transactions by "/transactions" request
    @GetMapping("/transactions")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<Transaction> getTransaction(){
        return transactionService.getAllTransactions();
    }

    //User can make transfer by "/send" request
    @PostMapping("/send")
    public ResponseEntity<?> makeTransaction(@RequestBody TransactionRequest transactionRequest) throws Exception {
        return transactionService.createTransaction(transactionRequest);
    }



}

