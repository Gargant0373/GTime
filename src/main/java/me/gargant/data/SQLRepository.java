package me.gargant.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import masecla.mlib.main.MLib;
import me.gargant.classes.LeaderboardItem;
import me.gargant.classes.Time;

@RequiredArgsConstructor
public class SQLRepository implements DataRepository {

    @NonNull
    private MLib lib;
    private boolean initialized = false;
    private Connection connection;

    @Override
    public void initialize() {
        String host = lib.getConfigurationAPI().getConfig().getString("sql.host");
        int port = lib.getConfigurationAPI().getConfig().getInt("sql.port");
        String name = lib.getConfigurationAPI().getConfig().getString("sql.database");
        String username = lib.getConfigurationAPI().getConfig().getString("sql.username");
        String password = lib.getConfigurationAPI().getConfig().getString("sql.password");

        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + name, username, password);
            this.createTable();
            this.initialized = true;
            lib.getLoggerAPI().information("SQL Connection initialized.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a table for the times if it doesn't exist.
     */
    private void createTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS times (uuid VARCHAR(36), map VARCHAR(255), time BIGINT, logged BIGINT, PRIMARY KEY (uuid, map), INDEX idx_uuid (uuid))";

        connection.prepareStatement(query).executeUpdate();
    }

    @Override
    public void saveTime(UUID uuid, Time time) {
        if (!initialized) {
            lib.getLoggerAPI().error("SQL not initialized. Cancelling request for save.");
            return;
        }

        String query = "REPLACE INTO times (uuid, map, time, logged) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement statement = (PreparedStatement) connection.prepareStatement(query);
            statement.setString(1, uuid.toString());
            statement.setString(2, time.getMap());
            statement.setLong(3, time.getTime());
            statement.setLong(4, System.currentTimeMillis());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Time getTime(UUID uuid, String map) {
        if (!initialized) {
            lib.getLoggerAPI().error("SQL not initialized. Cancelling request for time.");
            return null;
        }

        String query = "SELECT * FROM times WHERE uuid = ? AND map = ?";

        try {
            PreparedStatement statement = (PreparedStatement) connection.prepareStatement(query);
            statement.setString(1, uuid.toString());
            statement.setString(2, map);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                return new Time(map, set.getLong("time"), set.getLong("logged"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Time> getAllTimes(UUID uuid) {
        if (!initialized) {
            lib.getLoggerAPI().error("SQL not initialized. Cancelling request for times.");
            return null;
        }

        List<Time> times = new ArrayList<>();

        String query = "SELECT * FROM times WHERE uuid = ?";

        try {
            PreparedStatement statement = (PreparedStatement) connection.prepareStatement(query);
            statement.setString(1, uuid.toString());
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                String map = set.getString("map");
                long time = set.getLong("time");
                long logged = set.getLong("logged");
                times.add(new Time(map, time, logged));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return times;
    }

    @Override
    public List<LeaderboardItem> getTopTimes(String map) {
        if (!initialized) {
            lib.getLoggerAPI().error("SQL not initialized. Cancelling request for top times.");
            return null;
        }

        List<LeaderboardItem> times = new ArrayList<>();

        String query = "SELECT * FROM times WHERE map = ? ORDER BY time ASC LIMIT " + leaderboardSize();

        try {
            PreparedStatement statement = (PreparedStatement) connection.prepareStatement(query);
            statement.setString(1, map);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                UUID uuid = UUID.fromString(set.getString("uuid"));
                long time = set.getLong("time");
                times.add(new LeaderboardItem(uuid, new Time(map, time)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return times;
    }

    private int leaderboardSize() {
        return lib.getConfigurationAPI().getConfig().getInt("leaderboard.size", 10);
    }

}
