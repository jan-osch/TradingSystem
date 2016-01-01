package transactions.interactors;

import accounts.exceptions.AccountUuidNotFound;
import accounts.exceptions.AssetNotSufficient;
import accounts.exceptions.ResourcesNotSufficient;
import accounts.interactors.AccountsInteractor;
import markets.exceptions.InstrumentUuidNotFoundException;
import markets.exceptions.MarketNotFoundException;
import markets.interactors.MarketsInteractor;
import transactions.entities.TransactionManager;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TransactionsInteractor {

    private static TransactionManager transactionManager = TransactionManager.getInstance();

    public static void createStockBuyOrderByMarketPrice(UUID instrumentUUID, UUID accountUUID, BigDecimal amount) throws AccountUuidNotFound, MarketNotFoundException, InstrumentUuidNotFoundException, ResourcesNotSufficient {
        //todo: add logic
        BigDecimal balanceForAccount = AccountsInteractor.getBalanceForAccount(accountUUID);
        BigDecimal currentStockValue = MarketsInteractor.getCurrentStockValue(instrumentUUID);
        BigDecimal finalAmount = transactionManager.getTransactionAmount(balanceForAccount, currentStockValue, amount);
        AccountsInteractor.performBuyTransactionForAccount(accountUUID, instrumentUUID, finalAmount, finalAmount.multiply(currentStockValue));
    }

    public static void createStockSellOrderByMarketPrice(UUID instrumentUUID, UUID accountUUID, BigDecimal amount) throws AccountUuidNotFound, MarketNotFoundException, InstrumentUuidNotFoundException, AssetNotSufficient {
        //todo: add logic
        BigDecimal currentCount = AccountsInteractor.getAssetCountForAccount(accountUUID, instrumentUUID);
        BigDecimal actualCount = currentCount.min(amount);
        BigDecimal value = MarketsInteractor.getCurrentStockValue(instrumentUUID);
        AccountsInteractor.performSellTransactionForAccount(accountUUID, instrumentUUID, actualCount, value);
    }

    public static BigDecimal getTotalValueOfAccount(UUID accountUUID) throws AccountUuidNotFound, MarketNotFoundException, InstrumentUuidNotFoundException {
        Map<UUID, BigDecimal> portfolio = AccountsInteractor.getPortfolio(accountUUID);
        BigDecimal total = AccountsInteractor.getBalanceForAccount(accountUUID);
        BigDecimal partial;
        for (UUID uuid : portfolio.keySet()) {
            partial = MarketsInteractor.getCurrentStockValue(uuid);
            total = total.add(partial.multiply(portfolio.get(uuid)));
        }
        return total;
    }
}
