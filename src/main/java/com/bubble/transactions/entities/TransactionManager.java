package com.bubble.transactions.entities;

import java.math.BigDecimal;

public class TransactionManager {
    private static TransactionManager instance = null;

    private TransactionManager() {
    }

    public static TransactionManager getInstance() {
        if (instance == null) {
            instance = new TransactionManager();
        }
        return instance;
    }

    public BigDecimal getAmountOfShares(BigDecimal balanceForAccount, BigDecimal currentStockValue, BigDecimal count) {
        BigDecimal maximumForBalance = balanceForAccount.divideToIntegralValue(currentStockValue);
        if (maximumForBalance.compareTo(count) < 0) {
            return maximumForBalance;
        }
        return count;
    }
}
