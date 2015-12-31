package database;

import markets.entities.market.Market;
import markets.entities.market.MarketManager;
import markets.entities.record.HistoricalStockRecord;
import markets.entities.record.StockRecord;
import markets.exceptions.InstrumentUuidNotFoundException;
import markets.exceptions.MarketNotFoundException;
import persistance.CurrentStockRecordGateWay;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

public class PostgresCurrentStockRecordDao implements CurrentStockRecordGateWay {
    private final String tableName = "STOCK_RECORDS_CURRENT";
    private static PostgreSQLJDBC postgreSQLJDBC = PostgreSQLJDBC.getInstance();

    @Override
    public void save(StockRecord stockRecord) {
        String sql = "INSERT INTO \":tableName\" (UUID, DATE, VALUE) "
                + "VALUES (':uuid',':date', :value)";

        sql = SqlUtils.addParameterToSqlStatement(sql, "tableName", tableName);
        sql = SqlUtils.addParameterToSqlStatement(sql, "uuid", stockRecord.getInstrumentUuid().toString());
        sql = SqlUtils.addParameterToSqlStatement(sql, "date", stockRecord.getDate().toGMTString());
        sql = SqlUtils.addParameterToSqlStatement(sql, "value", stockRecord.getValue().toString());

        try {
            this.postgreSQLJDBC.executeSql(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Iterable<StockRecord> getCurrentRecordsForPeriod(UUID instrumentUuid, UUID marketUuid, Date startDate, Date endDate) {

        String sql = "SELECT * FROM \":tableName\" WHERE "
                + "uuid = ':uuid' AND date >= ':startDate' AND date <= ':endDate'";

        sql = SqlUtils.addParameterToSqlStatement(sql, "tableName", tableName);
        sql = SqlUtils.addParameterToSqlStatement(sql, "uuid", instrumentUuid.toString());
        sql = SqlUtils.addParameterToSqlStatement(sql, "startDate", startDate.toString());
        sql = SqlUtils.addParameterToSqlStatement(sql, "endDate", endDate.toString());

        LinkedList<StockRecord> result = new LinkedList<>();
        Market market;
        try {
            market = MarketManager.getMarketByUuid(marketUuid);
        } catch (MarketNotFoundException e) {
            e.printStackTrace();
            return result;
        }
        ResultSet resultSet = this.postgreSQLJDBC.executeSqlWithReturn(sql);
        try {
            while (resultSet.next()) {
                result.add(parseCurrentStockRecordFromResultSet(instrumentUuid, market, resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstrumentUuidNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    private StockRecord parseCurrentStockRecordFromResultSet(UUID instrumentUuid, Market market, ResultSet resultSet) throws SQLException, InstrumentUuidNotFoundException {
        Date date = resultSet.getTimestamp("date");
        BigDecimal value = resultSet.getBigDecimal("value");
        return new StockRecord(
                market.getInstrumentByUuid(instrumentUuid),
                value,
                date);
    }
}
