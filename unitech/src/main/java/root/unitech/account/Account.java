package root.unitech.account;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import root.unitech.auth.User;
import root.unitech.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts")
@Entity(name = "Account")
public class Account implements Serializable {
    @Id
    @SequenceGenerator(name = "account_id_seq", sequenceName = "account_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_id_seq")
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "is_active")
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    private AccountCurrency accountCurrency;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<Transaction> sentTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<Transaction> receivedTransactions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;


}
