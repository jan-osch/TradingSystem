package bubble.web.game.order;

import bubble.web.commons.IdHelper;
import bubble.web.game.account.Account;
import bubble.web.models.instrument.Index;

import java.util.Date;

public abstract class Order {
    private final int id;
    private final Account account;
    private final Index index;
    private final int numberOfShares;
    private final Date expirationDate;

    protected Order(Account account, Index index, int numberOfShares, Date expirationDate) {
        this.id = IdHelper.getInstance().getNewId();
        this.account = account;
        this.index = index;
        this.numberOfShares = numberOfShares;
        this.expirationDate = expirationDate;
    }

    public Account getAccount() {
        return account;
    }

    public Index getIndex() {
        return index;
    }

    public int getNumberOfShares() {
        return numberOfShares;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }
}