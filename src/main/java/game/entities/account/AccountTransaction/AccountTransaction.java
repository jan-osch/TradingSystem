package game.entities.account.AccountTransaction;

import java.math.BigDecimal;

public abstract class AccountTransaction {
    BigDecimal value;

    public BigDecimal getValue() {
        return value;
    }
}