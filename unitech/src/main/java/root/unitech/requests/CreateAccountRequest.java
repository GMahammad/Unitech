package root.unitech.requests;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {
    private String userPin;
    private BigDecimal balance;
    private String accountCurrency;
}
