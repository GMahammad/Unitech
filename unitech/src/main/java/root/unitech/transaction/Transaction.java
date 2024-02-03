package root.unitech.transaction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import root.unitech.account.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
@Entity(name = "Transaction")
public class Transaction {
    @Id
    @SequenceGenerator(name = "transaction_id_seq",sequenceName = "transaction_id_seq")
    @GeneratedValue(generator = "transaction_id_seq",strategy = GenerationType.SEQUENCE)
    @Column(name = "transaction_id",nullable = false)
    private Long transactionId;

    @Column(name = "transaction_balance",nullable = false)
    private BigDecimal transactionBalance;

    @Column(name ="created_at")
    private LocalDateTime createdAt;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "sender_account_number", referencedColumnName = "account_number")
    @JsonBackReference
    private Account sender;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "receiver_account_number", referencedColumnName = "account_number")
    @JsonBackReference
    private Account receiver;

}
