package root.unitech.account;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum AccountCurrency {
    AZN,
    USD,
    EUR,
    TL,
    RUB
}
