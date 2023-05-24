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
            this.initialized = true;
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
     * Creates a table for the player if it doesn't exist.
     * @param uuid the {@link UUID} of the player.
     */
    private void createTable(UUID uuid) {
        if (!initialized) {
            lib.getLoggerAPI().error("SQL not initialized. Cancelling request for table creation.");
            return;
        }

        String query = "CREATE TABLE IF NOT EXISTS " + uuid.toString().replace("-", "")
                + " (map VARCHAR(255) UNIQUE PRIMARY KEY, time BIGINT, logged BIGINT)";

        try {
            connection.prepareStatement(query).executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void saveTime(UUID uuid, Time time) {
        if (!initialized) {
            lib.getLoggerAPI().error("SQL not initialized. Cancelling request for save.");
            return;
        }

        createTable(uuid);

        String query = "REPLACE INTO " + uuid.toString().replace("-", "") + " (map, time, logged) VALUES (?, ?, ?)";

        try {
            PreparedStatement statement = (PreparedStatement) connection.prepareStatement(query);
            statement.setString(1, time.getMap());
            statement.setLong(2, time.getTime());
            statement.setLong(3, System.currentTimeMillis());
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

        createTable(uuid);

        String query = "SELECT time FROM " + uuid.toString().replace("-", "") + " WHERE map = ?";

        try {
            PreparedStatement statement = (PreparedStatement) connection.prepareStatement(query);
            statement.setString(1, map);
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

        createTable(uuid);

        List<Time> times = new ArrayList<>();

        // Kill me for this, I will switch to 2 primary keys l8r on.
        String query = "SELECT * FROM " + uuid.toString().replace("-", "");

        try {
            PreparedStatement statement = (PreparedStatement) connection.prepareStatement(query);
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

}
