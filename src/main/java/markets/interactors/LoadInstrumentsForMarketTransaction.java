package markets.interactors;

import com.google.common.collect.Iterables;
import commons.InteractorTransactionWithException;
import markets.entities.instrument.Index;
import markets.entities.instrument.Stock;
import markets.entities.market.Market;
import markets.entities.market.MarketManager;
import markets.exceptions.MarketNotFoundException;
import persistance.EntityGateWayManager;
import persistance.IndexGateWay;
import persistance.StockGateWay;

import java.util.UUID;

public class LoadInstrumentsForMarketTransaction implements InteractorTransactionWithException {
    private final UUID marketUuid;

    public LoadInstrumentsForMarketTransaction(UUID marketUuid) {
        this.marketUuid = marketUuid;
    }

    @Override
    public void execute() throws MarketNotFoundException {
        Market market = MarketManager.getMarketByUuid(marketUuid);

        loadStocks(market);

        loadIndices(market);
    }

    private void loadIndices(Market market) {
        IndexGateWay indexGateWay = EntityGateWayManager.getIndexGateWay();
        Iterable<Index> indices = indexGateWay.getIndicesByMarketUuid(this.marketUuid);
        if(indices!=null) {
            indices.forEach(market::addInstrument);
        }//todo remove this null check
    }

    private void loadStocks(Market market) {
        StockGateWay stockGateWay = EntityGateWayManager.getStockGateWay();
        Iterable<Stock> stocks = stockGateWay.getStocksByMarketUuid(this.marketUuid);
        stocks.forEach(market::addInstrument);
        System.out.printf("Successfully loaded: %d stocks to market: %s\n", Iterables.size(stocks), this.marketUuid);
    }
}