package com.bubble.database;


import com.bubble.commons.JsonHelper;
import com.bubble.game.entities.Game;
import com.bubble.persistance.GameGateWay;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PostgresGameDao implements GameGateWay {
    private final String tableName = "games";
    private PostgreSQLJDBC postgreSQLJDBC = PostgreSQLJDBC.getInstance();

    @Override
    public void save(Game game) {
        try {
            String sql = "INSERT INTO \":tableName\" VALUES (':uuid',':name', ':marketUuid', ':players', ':spectators', ':initial', ':active' )";
            sql = SqlUtils.addParameterToSqlStatement(sql, "tableName", tableName);
            sql = SqlUtils.addParameterToSqlStatement(sql, "uuid", game.gameUuid.toString());
            sql = SqlUtils.addParameterToSqlStatement(sql, "name", game.name);
            sql = SqlUtils.addParameterToSqlStatement(sql, "marketUuid", game.marketUuid.toString());
            sql = SqlUtils.addParameterToSqlStatement(sql, "players", JsonHelper.mapUuidUuidToJson(game.getPlayers()));
            sql = SqlUtils.addParameterToSqlStatement(sql, "spectators", SqlUtils.sqlLiteralStringFromList(game.getSpectators()));
            sql = SqlUtils.addParameterToSqlStatement(sql, "initial", game.getInitialValue().toString());
            sql = SqlUtils.addParameterToSqlStatement(sql, "active", String.valueOf(game.isActive()));
            System.out.println(sql);
            PostgreSQLJDBC.executeSql(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Game game) {
        deleteGame(game.gameUuid);
        save(game);
    }

    @Override
    public Iterable<Game> geAllGames() {
        ArrayList<Game> result = new ArrayList<>();
        String sql = "SELECT * FROM \":tableName\"";
        sql = SqlUtils.addParameterToSqlStatement(sql, "tableName", tableName);

        try {
            ResultSet resultSet = PostgreSQLJDBC.executeSqlWithReturn(sql);
            while (resultSet.next()) {
                result.add(parseGameFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Game parseGameFromResultSet(ResultSet resultSet) throws SQLException {
        String name = resultSet.getString("name");
        UUID uuid = (UUID) resultSet.getObject("uuid");
        UUID marketUuid = (UUID) resultSet.getObject("market_uuid");
        List<UUID> spectators = SqlUtils.getListFromString(resultSet.getString("spectators"));
        Map<UUID, UUID> players = JsonHelper.jsonToMapUuidUuid(resultSet.getString("players"));
        BigDecimal initial = resultSet.getBigDecimal("initial");
        Boolean active = resultSet.getBoolean("active");

        return new Game(name, uuid, marketUuid, players, spectators, initial, active);
    }

    @Override
    public Game getGameByUuid(UUID gameUuid) {
        String sql = "SELECT * FROM \":tableName\" WHERE uuid = ':uuid'";
        sql = SqlUtils.addParameterToSqlStatement(sql, "tableName", tableName);
        sql = SqlUtils.addParameterToSqlStatement(sql, "uuid", gameUuid.toString());
        try {
            ResultSet resultSet = PostgreSQLJDBC.executeSqlWithReturn(sql);
            resultSet.next();
            return parseGameFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void deleteGame(UUID gameUuid) {
        String sql = "DELETE FROM \":tableName\" WHERE uuid = ':uuid'";
        sql = SqlUtils.addParameterToSqlStatement(sql, "tableName", tableName);
        sql = SqlUtils.addParameterToSqlStatement(sql, "uuid", gameUuid.toString());
        try {
            PostgreSQLJDBC.executeSql(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
