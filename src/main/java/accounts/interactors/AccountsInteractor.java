package accounts.interactors;

import accounts.entities.Account;
import accounts.exceptions.AccountUuidNotFound;
import accounts.exceptions.AssetNotSufficient;
import accounts.exceptions.OwnerAlreadyHasAccount;
import accounts.exceptions.ResourcesNotSufficient;
import persistance.AccountGateWay;
import persistance.EntityGateWayManager;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccountsInteractor {

    private static AccountGateWay accountGateWay = EntityGateWayManager.getAccountGateWay();

    public static Account getAccount(UUID ownerUUID) throws AccountUuidNotFound {
        Account account = accountGateWay.getAccountByUuid(ownerUUID);
        if (account == null) {
            throw new AccountUuidNotFound();
        }
        return account;
    }

    public static Map<UUID, BigDecimal> getPortfolio(UUID ownerUUID) throws AccountUuidNotFound {
        Account account = getAccount(ownerUUID);
        return account.getPortfolio();
    }

    protected static void addResourcesToAccount(UUID ownerUUID, BigDecimal amount) throws AccountUuidNotFound {
        Account account = getAccount(ownerUUID);
        account.addResources(amount);
        accountGateWay.save(account);
    }

    protected static void subtractResources(UUID ownerUUID, BigDecimal amount) throws AccountUuidNotFound, ResourcesNotSufficient {
        Account account = getAccount(ownerUUID);
        account.subtractResources(amount);
        accountGateWay.save(account);
    }

    protected static void addAsset(UUID ownerUUID, UUID instrumentUUID, BigDecimal amount) throws AccountUuidNotFound {
        Account account = getAccount(ownerUUID);
        account.addAsset(instrumentUUID, amount);
        accountGateWay.save(account);
    }

    protected static void subtractAsset(UUID ownerUUID, UUID instrumentUUID, BigDecimal amount) throws AccountUuidNotFound, AssetNotSufficient {
        Account account = getAccount(ownerUUID);
        account.subtractAsset(instrumentUUID, amount);
        accountGateWay.save(account);
    }

    public static void createAccount(UUID ownerUUID, BigDecimal initalBalance) throws OwnerAlreadyHasAccount {
        try {
            getAccount(ownerUUID);
        } catch (AccountUuidNotFound accountUuidNotFound) {
            accountGateWay.save(new Account(ownerUUID, new HashMap<UUID, BigDecimal>(), initalBalance));
            return;
        }
        throw new OwnerAlreadyHasAccount();
    }

    public static BigDecimal getBalanceForAccount(UUID ownerUUID) throws AccountUuidNotFound {
        Account account = getAccount(ownerUUID);
        return account.getCurrentBalance();
    }

    public static BigDecimal getAssetCountForAccount(UUID ownerUUID, UUID instrumentUUID) throws AccountUuidNotFound {
        Account account = getAccount(ownerUUID);
        return account.getAssetCount(instrumentUUID);
    }

    public static void performBuyTransactionForAccount(UUID ownerUUID, UUID instrumentUUID, BigDecimal amount, BigDecimal value) throws AccountUuidNotFound, ResourcesNotSufficient {
        subtractResources(ownerUUID, value);
        addAsset(ownerUUID, instrumentUUID, amount);
    }

    public static void performSellTransactionForAccount(UUID ownerUUID, UUID instrumentUUID, BigDecimal amount, BigDecimal value) throws AccountUuidNotFound, AssetNotSufficient {
        addResourcesToAccount(ownerUUID, value);
        subtractAsset(ownerUUID, instrumentUUID, amount);
    }


}