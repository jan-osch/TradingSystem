package persistance;

import markets.entities.instrument.Stock;

import java.util.List;
import java.util.UUID;

public interface StockGateWay {
    void saveStock(Stock stock);

    Stock getStockByUuid(UUID uuid);

    Iterable<Stock> getStocksByMarketUuid(UUID marketUuid);

}